/*
 * Copyright 2015-2018 _floragunn_ GmbH
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Portions Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amazon.opendistroforelasticsearch.security.ssl;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.node.info.NodesInfoRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.PluginAwareNode;
import org.elasticsearch.transport.Netty4Plugin;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import com.amazon.opendistroforelasticsearch.security.ssl.util.SSLConfigConstants;

import io.netty.handler.ssl.OpenSsl;

public class OpenSSLTest extends SSLTest {

    @Before
    public void setup() {
        allowOpenSSL = true;
    }

    @Test
    public void testEnsureOpenSSLAvailability() {
        //Assert.assertTrue("OpenSSL not available: "+String.valueOf(OpenSsl.unavailabilityCause()), OpenSsl.isAvailable());
                
        final String openSSLOptional = System.getenv("OPENDISTRO_SECURITY_TEST_OPENSSL_OPT");
        System.out.println("OPENDISTRO_SECURITY_TEST_OPENSSL_OPT "+openSSLOptional);
        if(!Boolean.parseBoolean(openSSLOptional)) {
            System.out.println("OpenSSL must be available");
            Assert.assertTrue("OpenSSL not available: "+String.valueOf(OpenSsl.unavailabilityCause()), OpenSsl.isAvailable());
        } else {
            System.out.println("OpenSSL can be available");
        }
    }

    @Override
    @Test
    public void testHttps() throws Exception {
        Assume.assumeTrue(OpenSsl.isAvailable());
        super.testHttps();
    }

    @Override
    @Test
    public void testHttpsAndNodeSSL() throws Exception {
        Assume.assumeTrue(OpenSsl.isAvailable());
        super.testHttpsAndNodeSSL();
    }

    @Override
    @Test
    public void testHttpPlainFail() throws Exception {
        Assume.assumeTrue(OpenSsl.isAvailable());
        super.testHttpPlainFail();
    }

    @Override
    @Test
    public void testHttpsNoEnforce() throws Exception {
        Assume.assumeTrue(OpenSsl.isAvailable());
        super.testHttpsNoEnforce();
    }

    @Override
    @Test
    public void testHttpsV3Fail() throws Exception {
        Assume.assumeTrue(OpenSsl.isAvailable());
        super.testHttpsV3Fail();
    }

    @Override
    @Test(timeout=40000)
    public void testTransportClientSSL() throws Exception {
        Assume.assumeTrue(OpenSsl.isAvailable());
        super.testTransportClientSSL();
    }

    @Override
    @Test(timeout=40000)
    public void testNodeClientSSL() throws Exception {
        Assume.assumeTrue(OpenSsl.isAvailable());
        super.testNodeClientSSL();
    }

    @Override
    @Test(timeout=40000)
    public void testTransportClientSSLFail() throws Exception {
        Assume.assumeTrue(OpenSsl.isAvailable());
        super.testTransportClientSSLFail();
    }
    
    @Override
    @Test
    public void testHttpsOptionalAuth() throws Exception {
        Assume.assumeTrue(OpenSsl.isAvailable());
        super.testHttpsOptionalAuth();
    }
    
    @Test
    public void testAvailCiphersOpenSSL() throws Exception {
        Assume.assumeTrue(OpenSsl.isAvailable());

        // Set<String> openSSLAvailCiphers = new
        // HashSet<>(OpenSsl.availableCipherSuites());
        // System.out.println("OpenSSL available ciphers: "+openSSLAvailCiphers);
        // ECDHE-RSA-AES256-SHA, ECDH-ECDSA-AES256-SHA, DH-DSS-DES-CBC-SHA,
        // ADH-AES256-SHA256, ADH-CAMELLIA128-SHA

        final Set<String> openSSLSecureCiphers = new HashSet<>();
        for (final String secure : SSLConfigConstants.getSecureSSLCiphers(Settings.EMPTY, false)) {
            if (OpenSsl.isCipherSuiteAvailable(secure)) {
                openSSLSecureCiphers.add(secure);
            }
        }

        System.out.println("OpenSSL secure ciphers: " + openSSLSecureCiphers);
        Assert.assertTrue(openSSLSecureCiphers.size() > 0);
    }
    
    @Test
    public void testHttpsEnforceFail() throws Exception {
        Assume.assumeTrue(OpenSsl.isAvailable());
        super.testHttpsEnforceFail();
    }

    @Override
    public void testCipherAndProtocols() throws Exception {
        Assume.assumeTrue(OpenSsl.isAvailable());
        super.testCipherAndProtocols();
    }

    @Override
    public void testHttpsAndNodeSSLFailedCipher() throws Exception {
        Assume.assumeTrue(OpenSsl.isAvailable());
        super.testHttpsAndNodeSSLFailedCipher();
    }
    
    @Test
    public void testHttpsAndNodeSSLPem() throws Exception {
        Assume.assumeTrue(OpenSsl.isAvailable());
        super.testHttpsAndNodeSSLPem();
    }
    
    @Test
    public void testHttpsAndNodeSSLPemEnc() throws Exception {
        Assume.assumeTrue(OpenSsl.isAvailable());
        super.testHttpsAndNodeSSLPemEnc();
    }

    @Test
    public void testNodeClientSSLwithOpenSslTLSv13() throws Exception {

        Assume.assumeTrue(OpenSsl.isAvailable() && OpenSsl.version() > 0x10101009L);

        final Settings settings = Settings.builder().put("opendistro_security.ssl.transport.enabled", true)
                .put(SSLConfigConstants.OPENDISTRO_SECURITY_SSL_HTTP_ENABLE_OPENSSL_IF_AVAILABLE, allowOpenSSL)
                .put(SSLConfigConstants.OPENDISTRO_SECURITY_SSL_TRANSPORT_ENABLE_OPENSSL_IF_AVAILABLE, allowOpenSSL)
                .put(SSLConfigConstants.OPENDISTRO_SECURITY_SSL_TRANSPORT_KEYSTORE_ALIAS, "node-0")
                .put("opendistro_security.ssl.transport.keystore_filepath", getAbsoluteFilePathFromClassPath("node-0-keystore.jks"))
                .put("opendistro_security.ssl.transport.truststore_filepath", getAbsoluteFilePathFromClassPath("truststore.jks"))
                .put("opendistro_security.ssl.transport.enforce_hostname_verification", false)
                .put("opendistro_security.ssl.transport.resolve_hostname", false)
                .putList(SSLConfigConstants.OPENDISTRO_SECURITY_SSL_TRANSPORT_ENABLED_PROTOCOLS, "TLSv1.3")
                .putList(SSLConfigConstants.OPENDISTRO_SECURITY_SSL_TRANSPORT_ENABLED_CIPHERS, "TLS_CHACHA20_POLY1305_SHA256")
                .build();

        startES(settings);

        final Settings tcSettings = Settings.builder().put("cluster.name", clustername).put("path.home", ".")
                .put("node.name", "client_node_" + new Random().nextInt())
                .put(settings)// -----
                .build();

        try (Node node = new PluginAwareNode(false, tcSettings, Netty4Plugin.class, OpenDistroSecuritySSLPlugin.class).start()) {
            ClusterHealthResponse res = node.client().admin().cluster().health(new ClusterHealthRequest().waitForNodes("4").timeout(TimeValue.timeValueSeconds(5))).actionGet();
            Assert.assertFalse(res.isTimedOut());
            Assert.assertEquals(4, res.getNumberOfNodes());
            Assert.assertEquals(4, node.client().admin().cluster().nodesInfo(new NodesInfoRequest()).actionGet().getNodes().size());
        }

        Assert.assertFalse(executeSimpleRequest("_nodes/stats?pretty").contains("\"tx_size_in_bytes\" : 0"));
        Assert.assertFalse(executeSimpleRequest("_nodes/stats?pretty").contains("\"rx_count\" : 0"));
        Assert.assertFalse(executeSimpleRequest("_nodes/stats?pretty").contains("\"rx_size_in_bytes\" : 0"));
        Assert.assertFalse(executeSimpleRequest("_nodes/stats?pretty").contains("\"tx_count\" : 0"));
    }

}
