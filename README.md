SNE-XACML
=========
[![Build Status](https://travis-ci.org/canhnt/sne-xacml.svg?branch=master)](http://travis-ci.org/canhnt/sne-xacml)
[![Coverage Status](https://img.shields.io/coveralls/canhnt/sne-xacml.svg)](https://coveralls.io/r/canhnt/sne-xacml)

A high performance XACML 3.0 PDP Engine.

SNE-XACML engine applies Multi-data-type Interval Decision Diagram (MIDD) techniques to parse and transform OASIS XACML v3.0 policies into decision diagrams, which can substantially improve evaluation performance.

Author
------
Canh Ngo (<canhnt@gmail.com>)

Features
------
* XACML 3.0 core standard(*).
* Support multiple data types with comparable functions.
* Preserve original semantic of combining algorithms.
* Handle Indeterminate results in XACML 3.0
* Support critical attribute setting: attributes with `MustBePresent=true` property must be presented in requests, otherwise an error decision occurs. 

(\*) *Not all specs has been implemented*.

Usages
-------------
Include sne-xacml-core to the project's dependencies:
```
<dependency>
	<groupId>nl.uva.sne</groupId>
	<artifactId>sne-xacml-core</artifactId>
	<version>0.0.3</version>
</dependency>
```

Release Notes
-------------
###Version 0.0.3 - November 20, 2014
* Supported boolean and datetime nodes
* Used java generic types to instantiate nodes and edges
* Configured project to Travis, Jacoco & Coverage
* Published library to [Maven Central Repository](http://search.maven.org)

###Version 0.0.2 - November 19, 2013
* Compile library as a OSGi bundle
* Send debug messages to slf4j stream

###Version 0.0.1 - June 30, 2013

* Support data types: boolean, integer, double, string, anyURI, datetime, duration
* Combining algorithms: permit-overrides, deny-overrides, permit-unless-deny, deny-unless-permit, first-applicable, only-one-applicable.
* Comparison functions: eq, gt, lt, ge, le. 

Performance
-----------

Our experiments on a random 4-levels policy with 10 attributes, 360 rules show that the throughput of the engine is about 200,000 requests/s. 

References
----------
1. OASIS XACML 3.0: <http://docs.oasis-open.org/xacml/3.0/xacml-3.0-core-spec-os-en.html>
