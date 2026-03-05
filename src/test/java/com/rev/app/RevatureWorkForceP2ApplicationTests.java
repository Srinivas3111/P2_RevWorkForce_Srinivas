package com.rev.app;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("RevatureWorkForce – Full Test Suite")
@SelectPackages({
        "com.rev.app.controller",
        "com.rev.app.service",
        "com.rev.app.repository",
        "com.rev.app.rest"
})
class RevatureWorkForceP2ApplicationTests {

}
