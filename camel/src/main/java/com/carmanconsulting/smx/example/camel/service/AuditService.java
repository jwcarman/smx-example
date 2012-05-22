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

package com.carmanconsulting.smx.example.camel.service;

import org.apache.camel.Exchange;

public interface AuditService
{
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    public static final String BAM_PROCESS_TYPE_HEADER = "bam_process_type";
    public static final String BAM_PROCESS_ID_HEADER = "bam_process_id";
    public static final String BAM_ACTIVITY_ID_HEADER = "bam_activity_id";
    public static final String BAM_PARENT_ACTIVITY_ID_HEADER = "bam_parent_activity_id";

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    void auditExchange(Exchange exchange);
}
