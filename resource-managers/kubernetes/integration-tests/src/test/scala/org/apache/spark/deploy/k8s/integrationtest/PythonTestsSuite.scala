/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.spark.deploy.k8s.integrationtest

import org.apache.spark.deploy.k8s.integrationtest.TestConfig.{getTestImageRepo, getTestImageTag}

private[spark] trait PythonTestsSuite { k8sSuite: KubernetesSuite =>

  import PythonTestsSuite._
  import KubernetesSuite.k8sTestTag

  test("Run PySpark on simple pi.py example", k8sTestTag) {
    sparkAppConf
      .set("spark.kubernetes.container.image", s"${getTestImageRepo}/spark-py:${getTestImageTag}")
    runSparkApplicationAndVerifyCompletion(
      appResource = PYSPARK_PI,
      mainClass = "",
      expectedLogOnCompletion = Seq("Pi is roughly 3"),
      appArgs = Array("5"),
      driverPodChecker = doBasicDriverPyPodCheck,
      executorPodChecker = doBasicExecutorPyPodCheck,
      appLocator = appLocator,
      isJVM = false)
  }

  test("Run PySpark with Python2 to test a pyfiles example", k8sTestTag) {
    sparkAppConf
      .set("spark.kubernetes.container.image", s"${getTestImageRepo}/spark-py:${getTestImageTag}")
      .set("spark.kubernetes.pyspark.pythonVersion", "2")
    runSparkApplicationAndVerifyCompletion(
      appResource = PYSPARK_FILES,
      mainClass = "",
      expectedLogOnCompletion = Seq(
        "Python runtime version check is: True",
        "Python environment version check is: True"),
      appArgs = Array("python"),
      driverPodChecker = doBasicDriverPyPodCheck,
      executorPodChecker = doBasicExecutorPyPodCheck,
      appLocator = appLocator,
      isJVM = false,
      pyFiles = Some(PYSPARK_CONTAINER_TESTS))
  }

  test("Run PySpark with Python3 to test a pyfiles example", k8sTestTag) {
    sparkAppConf
      .set("spark.kubernetes.container.image", s"${getTestImageRepo}/spark-py:${getTestImageTag}")
      .set("spark.kubernetes.pyspark.pythonVersion", "3")
    runSparkApplicationAndVerifyCompletion(
      appResource = PYSPARK_FILES,
      mainClass = "",
      expectedLogOnCompletion = Seq(
        "Python runtime version check is: True",
        "Python environment version check is: True"),
      appArgs = Array("python3"),
      driverPodChecker = doBasicDriverPyPodCheck,
      executorPodChecker = doBasicExecutorPyPodCheck,
      appLocator = appLocator,
      isJVM = false,
      pyFiles = Some(PYSPARK_CONTAINER_TESTS))
  }
}

private[spark] object PythonTestsSuite {
  val CONTAINER_LOCAL_PYSPARK: String = "local:///opt/spark/examples/src/main/python/"
  val PYSPARK_PI: String = CONTAINER_LOCAL_PYSPARK + "pi.py"
  val PYSPARK_FILES: String = CONTAINER_LOCAL_PYSPARK + "pyfiles.py"
  val PYSPARK_CONTAINER_TESTS: String = CONTAINER_LOCAL_PYSPARK + "py_container_checks.py"
}

