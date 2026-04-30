/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0; you may not use this file except in compliance with the Elastic License
 * 2.0.
 */

package org.elasticsearch.xpack.esql.qa.csv;

import org.elasticsearch.test.rest.ESRestTestCase;
import org.elasticsearch.xpack.esql.datasources.FixtureUtils;

/**
 * Unit tests for {@link FixtureUtils#injectWithEntries}. Placed in the CSV QA project
 * because it already depends on the qa/server library where {@code FixtureUtils} lives.
 * Named with the {@code IT} suffix and extends {@link ESRestTestCase} to satisfy the
 * {@code javaRestTest} testing conventions, even though no cluster is required.
 */
public class FixtureUtilsIT extends ESRestTestCase {

    public void testInjectWithEntriesAppendsWhenNoExistingWith() {
        String result = FixtureUtils.injectWithEntries(
            "EXTERNAL \"s3://bucket/file.csv\"",
            "\"endpoint\": \"http://localhost\", \"key\": \"abc\""
        );
        assertEquals("EXTERNAL \"s3://bucket/file.csv\" WITH { \"endpoint\": \"http://localhost\", \"key\": \"abc\" }", result);
    }

    public void testInjectWithEntriesMergesIntoExistingWith() {
        String result = FixtureUtils.injectWithEntries(
            "EXTERNAL \"s3://bucket/file.csv\" WITH { \"header_row\": false }",
            "\"endpoint\": \"http://localhost\""
        );
        assertEquals("EXTERNAL \"s3://bucket/file.csv\" WITH { \"endpoint\": \"http://localhost\", \"header_row\": false }", result);
    }

    public void testInjectWithEntriesIgnoresWithInsideQuotedPath() {
        String result = FixtureUtils.injectWithEntries("EXTERNAL \"s3://bucket/WITH_data.csv\"", "\"endpoint\": \"http://localhost\"");
        assertEquals("EXTERNAL \"s3://bucket/WITH_data.csv\" WITH { \"endpoint\": \"http://localhost\" }", result);
    }

    public void testInjectWithEntriesIgnoresQuotedWithKeyword() {
        String result = FixtureUtils.injectWithEntries("EXTERNAL \"s3://bucket/WITH.csv\"", "\"key\": \"val\"");
        assertEquals("EXTERNAL \"s3://bucket/WITH.csv\" WITH { \"key\": \"val\" }", result);
    }

    public void testInjectWithEntriesCaseInsensitiveWith() {
        String result = FixtureUtils.injectWithEntries("EXTERNAL \"s3://b/f.csv\" with { \"existing\": true }", "\"new_key\": \"v\"");
        assertEquals("EXTERNAL \"s3://b/f.csv\" with { \"new_key\": \"v\", \"existing\": true }", result);
    }

    public void testInjectWithEntriesEmptyEntriesNoExistingWith() {
        String result = FixtureUtils.injectWithEntries("EXTERNAL \"s3://b/f.csv\"", "");
        assertEquals("EXTERNAL \"s3://b/f.csv\" WITH {  }", result);
    }

    public void testInjectWithEntriesEmptyEntriesWithExistingWith() {
        String result = FixtureUtils.injectWithEntries("EXTERNAL \"s3://b/f.csv\" WITH { \"header_row\": false }", "");
        assertEquals("EXTERNAL \"s3://b/f.csv\" WITH { \"header_row\": false }", result);
    }
}
