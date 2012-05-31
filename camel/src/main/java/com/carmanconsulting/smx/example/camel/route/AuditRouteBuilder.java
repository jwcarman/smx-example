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

public class AuditRouteBuilder extends AbstractRouteBuilder
{
    public AuditRouteBuilder()
    {
        setInputUri(DEFAULT_AUDIT_URI);
    }

    @Override
    protected void configureErrorHandling()
    {
        toDeadLetterOnly(Exception.class);
    }

    @Override
    protected void configureRoutes()
    {
        from(getInputUri()).to("log:audit?showAll=true&level=INFO&multiline=true");
    }
}
