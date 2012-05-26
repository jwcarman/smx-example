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

import com.carmanconsulting.smx.example.domain.entity.Activity;
import com.carmanconsulting.smx.example.domain.entity.Person;
import com.carmanconsulting.smx.example.domain.repository.PersonRepository;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.SimpleRegistry;
import org.easymock.Capture;
import org.junit.Test;
import static org.easymock.EasyMock.*;

public class TestNewPersonRouteBuilder extends AbstractRouteBuilderTest
{
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    private PersonRepository personRepository;

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception
    {
        return configure(new NewPersonRouteBuilder());
    }

    @Override
    protected void doBindings(SimpleRegistry registry)
    {
        personRepository = createMock(PersonRepository.class);
        registry.put("personRepository", personRepository);
    }

    @Test
    public void testHappyPath() throws Exception
    {
        Capture<Person> capture = new Capture<Person>();
        expect(personRepository.add(capture(capture))).andReturn(null);

        replayAll();
        getInputProducerTemplate().sendBody("Do Something!");

    }

    @Test
    public void testExceptionThrown() throws Exception
    {
        expect(personRepository.add(isA(Person.class))).andStubThrow(new RuntimeException("No way, Jose!"));
        replayAll();
        final MockEndpoint dlc = getMockEndpoint(DEAD_LETTER_URI);
        dlc.expectedMessageCount(1);
        final MockEndpoint audit = getMockEndpoint(AUDIT_URI);
        audit.expectedMessageCount(2);
        getInputProducerTemplate().asyncSendBody(INPUT_URI, "Do Something!");

        dlc.await();
        audit.await();
        Activity beginActivity = audit.getExchanges().get(0).getIn().getBody(Activity.class);
        Activity exceptionActivity = audit.getExchanges().get(1).getIn().getBody(Activity.class);
    }
}
