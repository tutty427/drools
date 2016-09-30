/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests;

import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import java.util.concurrent.atomic.AtomicInteger;

public class SubnetworkTest {

    @Test
    public void testRightStagingOnSharedSubnetwork() {
        // RHBRMS-2624
        String drl =
                "import " + AtomicInteger.class.getCanonicalName() + ";\n" +
                "rule R1y when\n" +
                "    AtomicInteger() \n" +
                "    Number() from accumulate ( AtomicInteger( ) and $s : String( ) ; count($s) )" +
                "    Long()\n" +
                "then\n" +
                "    System.out.println(\"R1\");" +
                "end\n" +
                "\n" +
                "rule R1x when\n" +
                "    AtomicInteger() \n" +
                "    Number() from accumulate ( AtomicInteger( ) and $s : String( ) ; count($s) )\n" +
                "then\n" +
                "    System.out.println(\"R1\");" +
                "end\n" +
                "" +
                "rule R2 when\n" +
                "    $i : AtomicInteger( get() < 3 )\n" +
                "then\n" +
                "    System.out.println(\"R2\");" +
                "    $i.incrementAndGet();" +
                "    update($i);" +
                "end\n";

        KieSession kieSession = new KieHelper().addContent( drl, ResourceType.DRL )
                                               .build().newKieSession();

        kieSession.insert( new AtomicInteger( 0 ) );
        kieSession.insert( "test" );

        kieSession.fireAllRules();
    }
}