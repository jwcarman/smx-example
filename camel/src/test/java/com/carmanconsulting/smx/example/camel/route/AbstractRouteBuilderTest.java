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

import org.apache.camel.CamelContext;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.easymock.*;
import org.junit.After;
import org.junit.Before;

import java.util.concurrent.TimeUnit;


public class AbstractRouteBuilderTest extends CamelTestSupport
{
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    public static final String INPUT_URI = "direct:input";
    public static final String OUTPUT_URI = "mock:output";
    public static final String DEAD_LETTER_URI = "mock:dlc";
    public static final String AUDIT_URI = "mock:audit";

    public static final int MAX_REDELIVERIES = 1;
    public static final int REDELIVERY_DELAY = 100;
    private final EasyMockSupport easyMockSupport = new EasyMockSupport();

    @Produce(uri = INPUT_URI)
    private ProducerTemplate inputProducerTemplate;

//----------------------------------------------------------------------------------------------------------------------
// Getter/Setter Methods
//----------------------------------------------------------------------------------------------------------------------

    public ProducerTemplate getInputProducerTemplate()
    {
        return inputProducerTemplate;
    }

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    protected <R extends AbstractRouteBuilder> R configure(R routeBuilder)
    {
        routeBuilder.setInputUri(INPUT_URI);
        routeBuilder.setAuditUri(AUDIT_URI);
        routeBuilder.setOutputUri(OUTPUT_URI);
        routeBuilder.setDeadLetterUri(DEAD_LETTER_URI);
        routeBuilder.setMaxRedeliveries(MAX_REDELIVERIES);
        routeBuilder.setRedeliveryDelay(REDELIVERY_DELAY);
        return routeBuilder;
    }

    @Override
    protected CamelContext createCamelContext() throws Exception
    {
        final SimpleRegistry registry = new SimpleRegistry();
        doBindings(registry);
        CamelContext context = new DefaultCamelContext(registry);
        context.setLazyLoadTypeConverters(isLazyLoadingTypeConverter());
        return context;
    }

    public IMocksControl createControl()
    {
        return easyMockSupport.createControl();
    }

    public <T> T createMock(Class<T> toMock)
    {
        return easyMockSupport.createMock(toMock);
    }

    public <T> T createMock(String name, Class<T> toMock)
    {
        return easyMockSupport.createMock(name, toMock);
    }

    public <T> IMockBuilder<T> createMockBuilder(Class<T> toMock)
    {
        return easyMockSupport.createMockBuilder(toMock);
    }

    public IMocksControl createNiceControl()
    {
        return easyMockSupport.createNiceControl();
    }

    public <T> T createNiceMock(Class<T> toMock)
    {
        return easyMockSupport.createNiceMock(toMock);
    }

    public <T> T createNiceMock(String name, Class<T> toMock)
    {
        return easyMockSupport.createNiceMock(name, toMock);
    }

    public IMocksControl createStrictControl()
    {
        return easyMockSupport.createStrictControl();
    }

    public <T> T createStrictMock(Class<T> toMock)
    {
        return easyMockSupport.createStrictMock(toMock);
    }

    public <T> T createStrictMock(String name, Class<T> toMock)
    {
        return easyMockSupport.createStrictMock(name, toMock);
    }

    protected void doBindings(SimpleRegistry registry)
    {
        // Do nothing!  Subclasses may override to provide bindings.
    }

    public void replayAll()
    {
        easyMockSupport.replayAll();
    }

    public void resetAll()
    {
        easyMockSupport.resetAll();
    }

    @Before
    public void resetAllMockObjects()
    {
        easyMockSupport.resetAll();
    }

    public void resetAllToDefault()
    {
        easyMockSupport.resetAllToDefault();
    }

    public void resetAllToNice()
    {
        easyMockSupport.resetAllToNice();
    }

    public void resetAllToStrict()
    {
        easyMockSupport.resetAllToStrict();
    }

    protected <T> CaptureAnswer<T> valueOf(Capture<T> capture)
    {
        return new CaptureAnswer<T>(capture);
    }

    public void verifyAll()
    {
        easyMockSupport.verifyAll();
    }

    @After
    public void verifyAllMockEndpoints() throws Exception
    {
        assertMockEndpointsSatisfied(2, TimeUnit.SECONDS);
    }

    @After
    public void verifyAllMockObjects()
    {
        verifyAll();
    }

//----------------------------------------------------------------------------------------------------------------------
// Inner Classes
//----------------------------------------------------------------------------------------------------------------------

    private static class CaptureAnswer<T> implements IAnswer<T>
    {
        private final Capture<T> capture;

        public CaptureAnswer(Capture<T> capture)
        {
            this.capture = capture;
        }

        @Override
        public T answer() throws Throwable
        {
            return capture.getValue();
        }
    }
}
