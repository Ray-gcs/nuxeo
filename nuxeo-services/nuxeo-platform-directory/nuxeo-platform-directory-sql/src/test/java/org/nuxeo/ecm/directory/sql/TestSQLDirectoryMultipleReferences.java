/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Florent Guillaume
 */
package org.nuxeo.ecm.directory.sql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.annotations.Granularity;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

/**
 * Tests where a field has several references bound to it.
 */
@RunWith(FeaturesRunner.class)
@Features(SQLDirectoryFeature.class)
@RepositoryConfig(cleanup = Granularity.METHOD)
@LocalDeploy({ "org.nuxeo.ecm.directory.sql.tests:test-sql-directories-schema-override.xml",
        "org.nuxeo.ecm.directory.sql.tests:test-sql-directories-bundle.xml",
        "org.nuxeo.ecm.directory.sql.tests:test-sql-directories-multi-refs.xml" })
public class TestSQLDirectoryMultipleReferences {

    protected static final String USER_DIR = "userDirectory";

    protected static final String GROUP_DIR = "groupDirectory";

    protected static final String SCHEMA = "user";

    @Inject
    protected DirectoryService directoryService;

    public Session getSession() throws Exception {
        return directoryService.open(USER_DIR);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetEntry() throws Exception {
        try (Session session = getSession()) {
            DocumentModel dm = session.getEntry("user_1");
            List<String> groups = (List<String>) dm.getProperty(SCHEMA, "groups");
            assertEquals(3, groups.size());
            assertTrue(groups.contains("group_1"));
            assertTrue(groups.contains("members"));
            assertTrue(groups.contains("powerusers")); // from second reference
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreateEntry() throws Exception {
        try (Session session = getSession()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("username", "user_0");
            // writing to groups, which has several references is ignored and a WARN logged
            map.put("groups", Arrays.asList("members", "administrators"));
            session.createEntry(map);
        }

        try (Session session = getSession()) {
            DocumentModel dm = session.getEntry("user_0");
            assertEquals("user_0", dm.getProperty(SCHEMA, "username"));
            List<String> groups = (List<String>) dm.getProperty(SCHEMA, "groups");
            // groups are unchanged
            assertEquals(0, groups.size());
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUpdateEntry() throws Exception {
        try (Session session = getSession()) {
            DocumentModel dm = session.getEntry("user_1");
            // update entry
            dm.setProperty(SCHEMA, "password", "pass_2");
            // writing to groups, which has several references is ignored and a WARN logged
            dm.setProperty(SCHEMA, "groups", Arrays.asList("administrators", "members"));
            session.updateEntry(dm);
        }

        try (Session session = getSession()) {
            DocumentModel dm = session.getEntry("user_1");
            assertEquals("user_1", dm.getProperty(SCHEMA, "username"));
            List<String> groups = (List<String>) dm.getProperty(SCHEMA, "groups");
            // groups are unchanged
            assertEquals(3, groups.size());
            assertTrue(groups.contains("group_1"));
            assertTrue(groups.contains("members"));
            assertTrue(groups.contains("powerusers")); // from second reference
        }
    }

}