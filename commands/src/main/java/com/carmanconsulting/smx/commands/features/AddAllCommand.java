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

package com.carmanconsulting.smx.commands.features;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;

import java.net.URI;
import java.util.List;

@Command(scope = "features", name = "addall", description = "Adds a list of repository URLs to the features service and installs all features contained within them.")
public class AddAllCommand extends AbstractFeaturesCommand
{
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    @Argument(index = 0, name = "urls", description = "One or more repository URLs separated by whitespaces", required = true, multiValued = true)
    List<String> urls;

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    @Override
    protected void doExecute(FeaturesService admin) throws Exception
    {
        for (String url : urls)
        {
            try
            {
                URI uri = new URI(url);
                admin.addRepository(uri);
                installFeatures(admin, uri);
            }
            catch (Exception e)
            {
                System.out.println("Could not add Feature Repository:\n" + e);
            }
        }
    }

    private void installFeatures(FeaturesService admin, URI repositoryUri) throws Exception
    {
        for (Feature feature : findRepository(admin, repositoryUri).getFeatures())
        {
            try
            {
                admin.installFeature(feature.getName());
            }
            catch (Exception e)
            {
                System.out.println("Unable to install feature " + feature.getName() + " from " + repositoryUri + ":\n" + e);
            }
        }
    }
}
