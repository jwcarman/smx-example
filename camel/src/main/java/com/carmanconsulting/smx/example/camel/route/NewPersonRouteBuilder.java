/*
 * Copyright (c) 2012 Carman Consulting, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.carmanconsulting.smx.example.camel.route;

import com.carmanconsulting.smx.example.domain.entity.Person;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class NewPersonRouteBuilder extends AbstractRouteBuilder
{
//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @Override
    protected void configureRoutes()
    {
        fromDefault()
                .process(beginProcess().processType("New Person"))
                .process(new Processor()
                {

                    @Override
                    public void process(Exchange exchange) throws Exception
                    {
                        final Person p = new Person();
                        p.setFirstName("Slappy");
                        p.setLastName("White");
                        exchange.getIn().setBody(p);
                    }
                })
                .to("bean:personRepository?method=add")
                .end();
    }
}
