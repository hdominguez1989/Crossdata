/*
 * Copyright (C) 2015 Stratio (http://stratio.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.stratio.crossdata.driver.config

import java.io.File
import java.util.concurrent.ConcurrentHashMap

import com.typesafe.config.{Config, ConfigFactory, ConfigValue, ConfigValueFactory}
import org.apache.log4j.Logger
import org.apache.spark.Logging

import scala.collection.JavaConversions._
import scala.util.Try

class DriverConf extends Logging {

  import DriverConf._

  val logger = Logger.getLogger(classOf[DriverConf])

  private val userSettings = new ConcurrentHashMap[String, ConfigValue]()

   def finalSettings: Config =
    userSettings.foldLeft(typesafeConf) { case (prevConfig, keyValue) =>
      prevConfig.withValue(keyValue._1, keyValue._2)
    }

  /**
   * Adds a generic key-value
   * akka => e.g akka.loglevel = "INFO"
   * driverConfig => e.g config.cluster.actor = "my-server-actor"
   */
  def set(key: String, value: ConfigValue): DriverConf = {
    userSettings.put(key, value)
    this
  }

  def setAll(settings: Traversable[(String, ConfigValue)]): DriverConf = {
    settings.foreach { case (k, v) => set(k, v) }
    this
  }

  /**
   * @param hostAndPort e.g 127.0.0.1:13420
   */
  def setClusterContactPoint(hostAndPort: String*): DriverConf = {
    userSettings.put(DriverConfigHosts, ConfigValueFactory.fromIterable(hostAndPort))
    this
  }

  /**
   * @param hostAndPort e.g 127.0.0.1:13420
   */
  def setClusterContactPoint(hostAndPort: java.util.List[String]): DriverConf = setClusterContactPoint(hostAndPort:_*)

  def setHttpHostAndPort(host: String, port: Int): DriverConf = {
    val newSettings = Seq(
      DriverConf.Http.Host -> ConfigValueFactory.fromAnyRef(host),
      DriverConf.Http.Port -> ConfigValueFactory.fromAnyRef(port)
    )
    setAll(newSettings)
  }

  def setFlattenTables(flatten: Boolean): DriverConf = {
    userSettings.put(DriverFlattenTables, ConfigValueFactory.fromAnyRef(flatten))
    this
  }

  def setTunnelTimeout(seconds: Int): DriverConf = {
    userSettings.put(AkkaClusterRecepcionistTunnelTimeout, ConfigValueFactory.fromAnyRef(seconds * 1000))
    this
  }

  def setEnableSsl(enabled: Boolean): DriverConf = {
    userSettings.put(Http.TLS.TlsEnable, ConfigValueFactory.fromAnyRef(enabled))
    this
  }

  def setKeyStorePath(keyStorePath: String): DriverConf = {
    userSettings.put(Http.TLS.TlsKeyStore, ConfigValueFactory.fromAnyRef(keyStorePath))
    this
  }

  def setKeyStorePwd(keyStorePwd: String): DriverConf = {
    userSettings.put(Http.TLS.TlsKeystorePwd, ConfigValueFactory.fromAnyRef(keyStorePwd))
    this
  }

  def setTrustStorePath(trustStorePath: String): DriverConf = {
    userSettings.put(Http.TLS.TlsTrustStore, ConfigValueFactory.fromAnyRef(trustStorePath))
    this
  }

  def setTrustStorePwd(trustStorePwd: String): DriverConf = {
    userSettings.put(Http.TLS.TlsTrustStorePwd, ConfigValueFactory.fromAnyRef(trustStorePwd))
    this
  }

  def setHttpClientIdleTimeout(seconds: Int): DriverConf = {
    userSettings.put(Http.ClientIdleTimeout, ConfigValueFactory.fromAnyRef(seconds * 1000))
    this
  }

  def setHttpHostConnectionPoolClientIdleTimeout(seconds: Int): DriverConf = {
    userSettings.put(Http.HostConnectionPoolClientIdleTimeout, ConfigValueFactory.fromAnyRef(seconds * 1000))
    this
  }

  private[crossdata] def get(key: String): AnyRef = {
    getOption(key).getOrElse(throw new NoSuchElementException(key))
  }

  private[crossdata] def getOption(key: String): Option[AnyRef] = {
    Option(finalSettings.getAnyRef(key))
  }

  private[crossdata] def getClusterContactPoint: List[String] = {
    val hosts = finalSettings.getStringList(DriverConfigHosts).toList
    val clusterName = finalSettings.getString(DriverClusterName)
    val ssl= Try(finalSettings.getBoolean(SSLEnabled)).getOrElse(false)
    if (ssl)
      hosts map (host => s"akka.ssl.tcp://$clusterName@$host$ActorsPath")
    else
      hosts map (host => s"akka.tcp://$clusterName@$host$ActorsPath")
  }

  private[crossdata] def getCrossdataServerHttp: String = {
    val host = finalSettings.getString(Http.Host)
    val port = finalSettings.getInt(Http.Port)
    s"$host:$port"
  }

  private[crossdata] def getFlattenTables: Boolean =
    finalSettings.getBoolean(DriverFlattenTables)


  private val typesafeConf: Config = {

    val defaultConfig = ConfigFactory.load(DriverConfigDefault).getConfig(ParentConfigName)

    //Get the driver-application.conf properties if exists in resources
    val configWithResource: Config = {

      val configResource = defaultConfig.getString(DriverConfigResource)
      val resource = DriverConf.getClass.getClassLoader.getResource(configResource)
      Option(resource).fold {
        logger.warn("User resource (" + configResource + ") haven't been found")
        val file = new File(configResource)
        if (file.exists()) {
          val userConfig = ConfigFactory.parseFile(file).getConfig(ParentConfigName)
          userConfig.withFallback(defaultConfig)
        } else {
          logger.warn("User file (" + configResource + ") haven't been found in classpath")
          defaultConfig
        }
      } { resTemp =>
        val userConfig = ConfigFactory.parseResources(configResource).getConfig(ParentConfigName)
        userConfig.withFallback(defaultConfig)
      }
    }

    //Get the user external driver-application.conf properties if exists
    val finalConfig: Config = {

      val configFile = {
        val envConfigFile = Option(System.getProperties.getProperty(DriverConfigFile))
        envConfigFile.getOrElse(defaultConfig.getString(DriverConfigFile))
      }

      if (configFile.isEmpty) {
        configWithResource
      } else {
        val file = new File(configFile)
        if (file.exists()) {
          val parsedConfig = ConfigFactory.parseFile(file)
          if(parsedConfig.hasPath(ParentConfigName)){
            val userConfig = ConfigFactory.parseFile(file).getConfig(ParentConfigName)
            userConfig.withFallback(configWithResource)
          } else {
            logger.warn(s"User file ($configFile) found but not configuration found under $ParentConfigName")
            configWithResource
          }
        } else {
          logger.warn("User file (" + configFile + ") haven't been found")
          configWithResource
        }
      }
    }

    // System properties
    val systemPropertiesConfig =
      Try(
        ConfigFactory.parseProperties(System.getProperties).getConfig(ParentConfigName)
      ).getOrElse(
        ConfigFactory.parseProperties(System.getProperties)
      )

    val finalConfigWithSystemProperties = systemPropertiesConfig.withFallback(finalConfig)

    val finalConfigWithEnvVars=
      Try(finalConfigWithSystemProperties.getString(DriverConfigHosts)).map{
        strCluster => finalConfigWithSystemProperties.withValue(
          DriverConfigHosts,
          ConfigValueFactory.fromIterable(strCluster.split(",").toList)
        )
      }.getOrElse(finalConfigWithSystemProperties)

    logger.debug(s"Cluster.hosts = ${finalConfigWithEnvVars.getAnyRef(DriverConfigHosts)}")

    ConfigFactory.load(finalConfigWithEnvVars)
  }

  def httpTlsEnable =
    finalSettings.getBoolean(DriverConf.Http.TLS.TlsEnable)

  def httpTlsTrustStore =
    finalSettings.getString(DriverConf.Http.TLS.TlsTrustStore)

  def httpTlsTrustStorePwd =
    finalSettings.getString(DriverConf.Http.TLS.TlsTrustStorePwd)

  def httpTlsKeyStore =
    finalSettings.getString(DriverConf.Http.TLS.TlsKeyStore)

  def httpTlsKeyStorePwd =
    finalSettings.getString(DriverConf.Http.TLS.TlsKeystorePwd)

}


object DriverConf {
  val ActorsPath = "/system/receptionist"
  val DriverConfigDefault = "driver-reference.conf"
  val ParentConfigName = "crossdata-driver"
  val DriverConfigResource = "external.config.resource"
  val DriverConfigFile = "external.config.filename"
  val DriverConfigHosts = "config.cluster.hosts"
  val DriverFlattenTables = "config.flatten-tables"
  val DriverClusterName = "config.cluster.name"
  val SSLEnabled = "akka.remote.netty.ssl.enable-ssl"
  val AkkaClusterRecepcionistTunnelTimeout = "akka.contrib.cluster.receptionist.response-tunnel-receive-timeout"

  // Http Server
  object Http { //TODO: Server-Driver labels objects unification/de-duplication ?

    val Host = "akka-http.host"
    val Port = "akka-http.port"

    val HostConnectionPoolClientIdleTimeout = "akka.http.host-connection-pool.client.idle-timeout"
    val ClientIdleTimeout = "akka.http.client.idle-timeout"

    //TLS akka-http client authentication
    object TLS {
      val TlsEnable = "akka-http.ssl.enable"
      val TlsTrustStore = "akka-http.ssl.truststore"
      val TlsTrustStorePwd = "akka-http.ssl.truststore-password"
      val TlsKeyStore = "akka-http.ssl.keystore"
      val TlsKeystorePwd = "akka-http.ssl.keystore-password"
    }

  }
}