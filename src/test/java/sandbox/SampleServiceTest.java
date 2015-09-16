/*******************************************************************************
 * ADOBE CONFIDENTIAL
 * __________________
 *
 * Copyright 2015 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Adobe Systems Incorporated and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Adobe Systems Incorporated and its
 * suppliers and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Adobe Systems Incorporated.
 ******************************************************************************/

package sandbox;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.testing.jcr.RepositoryUtil;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.apache.sling.testing.mock.sling.junit.SlingContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Your comment here
 */
public class SampleServiceTest {

    @Rule
    public SlingContext context = new SlingContext(ResourceResolverType.JCR_JACKRABBIT);


    @Before
    public void prepare() throws IOException, RepositoryException {
        Session adminSession = context.resourceResolver().adaptTo(Session.class);
        RepositoryUtil.registerSlingNodeTypes(adminSession);

        Resource content = context.resourceResolver().getResource("/content");
        if (content == null) {
            context.load().json("/sample-nodes.json","/content/sample-node-1");
        }
    }

    @Test
    public void testQueryNoCondition() throws RepositoryException {
        int expectedResults = 1;
        String queryString = "SELECT * FROM [nt:unstructured] AS N";
        List<String> actualResults = executeQuery(context.resourceResolver(),queryString);
        Assert.assertEquals("The number of results without parameter is incorrect", expectedResults, actualResults.size());
    }

    @Test
    public void testQueryWithCondition() throws RepositoryException {
        int expectedResults = 1;
        String queryString =
                "SELECT * FROM [nt:unstructured] AS N WHERE ISDESCENDANTNODE(N,\"/content\")";
        List<String> actualResults = executeQuery(context.resourceResolver(), queryString);
        Assert.assertEquals("The number of results is incorrect", expectedResults, actualResults.size());
    }

    private List<String> executeQuery(ResourceResolver resolver, String queryString) throws RepositoryException {
        List<String> results = new ArrayList<String>();
        QueryManager qMgr = resolver.adaptTo(Session.class).getWorkspace().getQueryManager();
        Query query = qMgr.createQuery(queryString, Query.JCR_SQL2);
        QueryResult r = query.execute();

        for (NodeIterator it = r.getNodes(); it.hasNext(); ) {

            Node item = it.nextNode();
            results.add(item.getPath());

        }
        return results;
    }

}
