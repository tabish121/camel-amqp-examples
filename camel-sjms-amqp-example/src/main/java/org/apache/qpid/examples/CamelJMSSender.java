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
package org.apache.qpid.examples;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.sjms.SjmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.qpid.jms.JmsConnectionFactory;

public class CamelJMSSender {

    public void runExample() throws Exception {
        JmsConnectionFactory cf = new JmsConnectionFactory("amqp://localhost:5672");

        SjmsComponent component = new SjmsComponent();
        component.setConnectionFactory(cf);

        CamelContext context = new DefaultCamelContext();
        context.addComponent("sjms", component);
        context.addRoutes(new RouteBuilder() {

            @Override
            public void configure() {
                from("direct:start").to("sjms:queue:camel-sjms-example");
            }
        });

        ProducerTemplate template = context.createProducerTemplate();

        context.start();

        Exception failure = null;

        // Send 10 messages, with reports of any failures
        for (int i = 0; i < 10; i++) {
            try {
                template.sendBody("direct:start", "Test Message: " + i);
            } catch (Exception jmsEx) {
                System.out.print("Failed sending message: " + i);
                failure = jmsEx;
            }
        }

        context.stop();

        if (failure != null) {
            throw failure;
        }
    }

    public static void main(String[] args) {
        System.out.println("Running Apache Camel SJMS AMQP Sender example");

        CamelJMSSender example = new CamelJMSSender();

        try {
            example.runExample();
        } catch (Exception ex) {
            System.out.println("Error in Apache Camel SJMS AMQP Sender example: " + ex.getMessage());
        }

        System.out.println("Completed Apache Camel SJMS AMQP Sender example");
    }
}
