/*
 *  Copyright 2015 Adobe Systems Incorporated
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.core.components.core.servlets;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.EmptyDataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.*;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Servlet that writes some sample content into the response. It is mounted for
 * all resources of a specific Sling resource type. The
 * {@link SlingSafeMethodsServlet} shall be used for HTTP methods that are
 * idempotent. For write operations use the {@link SlingAllMethodsServlet}.
 */
@Component(service = Servlet.class, immediate = true, property = {
        Constants.SERVICE_DESCRIPTION + "=Promo forum component Dropdown option fetch Servlet",
        "sling.servlet.paths=/bin/dialogdropdown", "sling.servlet.methods=" + HttpConstants.METHOD_GET })
public class DailogDropdown extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;

    Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String  OPTION_FILTER_REGEX = "filter";
    private static final String CQ_CLIENTLIBRARY_FOLDER = "cq:ClientLibraryFolder";

    private Pattern pattern;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {
        // logger.info("Before try");
        try {

            if(request.getResource().getValueMap().containsKey("paths") && request.getResource().getValueMap().containsKey("filter")){
                String[] paths = (String[]) request.getResource().getValueMap().get("paths");
                String filterRegex = request.getResource().getValueMap().get(OPTION_FILTER_REGEX).toString();
                pattern = Pattern.compile(filterRegex);
                ResourceResolver resourceResolver = request.getResourceResolver();
                request.setAttribute(DataSource.class.getName(), EmptyDataSource.instance());
                ResourceResolver resolver = request.getResourceResolver();
                // Create an ArrayList to hold data
                List<Resource> fakeResourceList = new ArrayList<Resource>();
                for (String path : paths) {
                    Resource resource = resourceResolver.getResource(path);
                    if (resource == null) {
                        return;
                    }
                    for (Resource child : resource.getChildren()) {
                        if (StringUtils.equals(child.getResourceType(), CQ_CLIENTLIBRARY_FOLDER)) {
                            String[] categories = (String[]) child.getValueMap().get("categories");
                            for (String category : categories) {
                                Matcher matcher = pattern.matcher(category);
                                if (matcher.matches()){
                                    ValueMap vm = new ValueMapDecorator(new HashMap<String, Object>());
                                vm.put("value",category );
                                vm.put("text", category.replaceAll(".theme",""));
                                    fakeResourceList.add(new ValueMapResource(resolver, new ResourceMetadata(), "nt:unstructured", vm));
                                }

                            }

                        }

                    }
                }
                DataSource ds = new SimpleDataSource(fakeResourceList.iterator());
                request.setAttribute(DataSource.class.getName(), ds);

            }


            // Create a DataSource that is used to populate the drop-down control

        } catch (Exception e) {
            logger.info("Error in Get Drop Down Values", e);
        }
    }


}
