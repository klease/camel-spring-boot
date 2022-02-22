/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.servlet.springboot.test;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

/**
 * Testing HttpMessage used with Servlet component.
 */
@CamelSpringBootTest
@SpringBootApplication
@DirtiesContext
@ContextConfiguration(classes = ServletHttpMessageTest.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ServletHttpMessageTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CamelContext context;

    @BeforeEach
    public void setup() throws Exception {
        context.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                rest().get("/outMessageNullBody")
                        .produces("text/plain")
                        .to("direct:nullbody");

                from("direct:nullbody")
                        // read body at least once
                        .log("${body}")
                        // simulate endpoints that may put null to out message body
                        .process(e -> e.getOut().setBody(null))
                        // ensure reading body again does not cause an exception
                        .log("${body}");
            }
        });
    }

    @Test
    public void testOutMessageNullBody() {
        Assertions.assertNull(restTemplate.getForEntity("/camel/outMessageNullBody", String.class).getBody());
    }

}

