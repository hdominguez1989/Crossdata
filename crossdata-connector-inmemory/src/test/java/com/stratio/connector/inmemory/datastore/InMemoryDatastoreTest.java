/*
 * Licensed to STRATIO (C) under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.  The STRATIO (C) licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.stratio.connector.inmemory.datastore;

import static org.testng.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeClass;

/**
 * Datastore tests.
 */
public class InMemoryDatastoreTest {

    private final String CATALOG_NAME = "test_catalog";

    private final String TEST_TABLE1 = "table1";

    private final InMemoryDatastore db = new InMemoryDatastore(InMemoryTableTest.TABLE_MAX_ROWS);

    @BeforeClass
    public void setUp() {
        db.createCatalog(CATALOG_NAME);
        String[] columnNames = new String[] { "string_col", "integer_col", "boolean_col" };
        Class[] columnTypes = new Class[] { String.class, Integer.class, Boolean.class };
        List<String> primaryKey = Arrays.asList("string_col");
        try {
            db.createTable(CATALOG_NAME, TEST_TABLE1, columnNames, columnTypes, primaryKey);
        } catch (Exception e) {
            fail("Cannot create test table", e);
        }

        //Add test data
        try {
            Map<String, Object> row;
            for (int index = 0; index < InMemoryTableTest.INSERT_TEST_SIZE; index++) {
                row = new HashMap<>();
                row.put(columnNames[0], columnNames[0] + index);
                row.put(columnNames[1], index);
                row.put(columnNames[2], index % 2 == 0);
                db.insert(CATALOG_NAME, TEST_TABLE1, row);
            }
        } catch (Exception e) {
            fail("Insert should work", e);
        }
    }

}
