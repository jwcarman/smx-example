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

@Command(scope = "features", name = "removeall", description = "Removes the given list of repository URLs from the features service.")
public class RemoveAllCommand extends AbstractFeaturesCommand
{
//----------------------------------------------------------------------------------------------------------------------
// Fields
//----------------------------------------------------------------------------------------------------------------------

    @Argument(index = 0, name = "urls", description = "One or more repository URLs separated by whitespaces", required = true, multiValued = true)
    List<String> urls;

//----------------------------------------------------------------------------------------------------------------------
// Other Methods
//----------------------------------------------------------------------------------------------------------------------

    protected void doExecute(FeaturesService admin) throws Exception
    {
        for (String url : urls)
        {
            URI repositoryUri = new URI(url);
            Feature[] features = findRepository(admin, repositoryUri).getFeatures();
            for (int i = features.length - 1; i >= 0; i--)
            {
                Feature feature = features[i];
                if(admin.isInstalled(feature))
                {
                    System.out.println("Uninstalling feature " + feature.getName() + "...");
                    admin.uninstallFeature(feature.getName());
                }
            }
            System.out.println("Removing feature repository " + repositoryUri + "...");
            admin.removeRepository(repositoryUri);
        }
    }
}

