# Supported Deployment Models

## Single Node Deployments

### Deploying the Mangle Virtual Appliance

For a quick POC we recommend that you deploy a single node instance of Mangle using the OVA file that we have made available [here](https://repo.vmware.com/mangle/v2.0.1/Mangle-2.0.1_OVF10.ova).

Using the OVA is a fast and easy way to create a Mangle VM on VMware vSphere.

After you have downloaded the OVA, log in to your vSphere environment and perform the following steps:

1. Start the Import Process

   From the Actions pull-down menu for a datacenter, choose **Deploy OVF Template**.

   ![Create/Register VM](../../.gitbook/assets/step1_deploy-ovf-template.png)

   Provide the location of the downloaded ova file.

   Choose **Next**.

2. Specify the name and location of virtual machine                      

   Enter a name for the virtual machine and select a location for the virtual machine.

   ![OVA file](../../.gitbook/assets/step2_select-name.png)

   Choose **Next**.

3. Specify the compute resource

   Select a cluster, host or resource pool where the virtual machine needs to be deployed.

   ![Target datastore](../../.gitbook/assets/step3_select-compute-resource.png)

   Choose **Next**.

4. Review details

   ![](../../.gitbook/assets/step4_review-details.png) 

   Choose **Next**.

5. Accept License Agreement

   Read through the Mangle License Agreement, and then choose **I accept all license agreements**.

   ![License](../../.gitbook/assets/step5_accept-license.png)

   Choose **Next**.

6. Select Storage

   Mangle is provisioned with a maximum disk size. By default, Mangle uses only the portion of disk space that it needs, usually much less that the entire disk size \( **Thin** client\). If you want to pre-allocate the entire disk size \(reserving it entirely for Mangle instead\), select **Thick** instead.

   ![Deployment Options](../../.gitbook/assets/step6_select-storage.png)

   Choose **Next**.

7. Select Network

   Provide a static or dhcp IP for Mangle after choosing an appropriate destination network. ![](../../.gitbook/assets/step7_select-network.png) 

   ![](../../.gitbook/assets/step8_customize-template.png) 

   Choose **Next**.

8. Verify Deployment Settings and click **Finish** to start creating the virtual machine. Depending on bandwidth, this operation might take a while. When finished, vSphere powers up the Mangle VM based on your selections.

After the VM is boots up successfully, open the command window. Press Alt+F2 to log into the VM.

The default account credentials are:

* Username: `root`
* Password: `vmware`

{% hint style="info" %}
Because of limitations within OVA support on vSphere, it was necessary to specify a default password for the OVA option. However, for security reasons, we would recommend that you modify the password after importing the appliance.
{% endhint %}

It takes a couple of minutes for the containers to run. Once the Mangle application and DB containers are running, the Mangle application should be available at the following URL: 

```text
https://<IP or Hostname provided>/mangle-services
```

You will be prompted to change the admin password to continue.

* Default Mangle Username: `admin@mangle.local`
* Password: `admin`

Export the VM as a Template \(Optional\)

Consider converting this imported VM into a template \(from the Actions menu, choose **Export** \) so that you have a master Mangle instance that can be combined with vSphere Guest Customization to enable rapid provisioning of Mangle instances.

Now you can move on to the [Mangle Users Guide](../../sre-developers-and-users/).

### Deploying the Mangle Containers

#### Prerequisites

Before creating the Mangle container a Cassandra DB container should be made available on a Docker host. You can choose to deploy the DB and the Application container on the same Docker host or on different Docker hosts. However, we recommend that you use a separate Docker host for each of these. You can setup a Docker host by following the instructions [here](https://docs.docker.com/install/).

To deploy Cassandra, you can either use the authentication enabled image tested and verified with Mangle available on the Mangle Docker repo or use the default public Cassandra image hosted on Dockerhub.

**If you chose to use the Cassandra image from Mangle Docker Repo:**

```text
docker run --name mangle-cassandradb -v /cassandra/storage/:/var/lib/cassandra -p 9042:9042 -d -e CASSANDRA_CLUSTER_NAME="manglecassandracluster" -e CASSANDRA_DC="DC1" -e CASSANDRA_RACK="rack1" -e CASSANDRA_ENDPOINT_SNITCH="GossipingPropertyFileSnitch"  mangleuser/mangle_cassandradb:<VERSION>
```

**If you chose to use the Cassandra image from** [**Dockerhub**](https://hub.docker.com/_/cassandra/)**:**

```text
docker run --name mangle-cassandradb -v /cassandra/storage/:/var/lib/cassandra -p 9042:9042 -d -e CASSANDRA_CLUSTER_NAME="manglecassandracluster" -e CASSANDRA_DC="DC1" -e CASSANDRA_RACK="rack1" -e CASSANDRA_ENDPOINT_SNITCH="GossipingPropertyFileSnitch" cassandra:3.11
```

{% hint style="info" %}
To enable authentication or clustering on Cassandra refer to the [Cassandra Advanced Configuration](advanced-cassandra-configuration.md).
{% endhint %}

#### **Deploying the Mangle application container**

To deploy the Mangle container using a Cassandra DB deployed using the image from Mangle Docker repo or with DB authentication and ssl enabled, run the docker command below on the docker host after substituting the values in angle braces &lt;&gt; with actual values.

```text
docker run --name mangle -d -e DB_OPTIONS="-DcassandraContactPoints=<IP of the docker host where Cassandra is deployed> -DcassandraSslEnabled=true -DcassandraUsername=cassandra -DcassandraPassword=cassandra" -e CLUSTER_OPTIONS="-DclusterValidationToken=mangle -DpublicAddress=<IP or Hostname>" -p 8080:8080 -p 8443:8443 mangleuser/mangle:<VERSION>
```

To deploy the Mangle container using a Cassandra DB deployed using the image from Dockerhub or with DB authentication and ssl disabled, run the docker command below on the docker host after substituting the values in angle braces &lt;&gt; with actual values.

```text
docker run --name mangle -d -e DB_OPTIONS="-DcassandraContactPoints=<IP of the docker host where Cassandra is deployed> -DcassandraSslEnabled=false" -e CLUSTER_OPTIONS="-DclusterValidationToken=mangle -DpublicAddress=<IP or Hostname>" -p 8080:8080 -p 8443:8443 mangleuser/mangle:<VERSION>
```

{% hint style="info" %}
The Mangle docker container takes two environmental variables 

"**DB\_OPTIONS**", which can take a list of java arguments identifying the properties of the database cluster

"**CLUSTER\_OPTIONS**", which can take a list of java arguments identifying the properties of the Mangle application cluster

Although the docker run commands above lists only a few DB\_OPTIONS and CLUSTER\_OPTIONS parameters, Mangle supports a lot more for further customization.
{% endhint %}

**Supported DB\_OPTIONS**

```text
-DcassandraContactPoints : IP Address of Cassandra DB (mandatory, default value is "localhost" and works only if the Mangle application and DB are on the same Docker host)
-DcassandraClusterName : Cassandra cluster name (mandatory, value should be the one passed during cassandra db creation as an environmental variable CASSANDRA_CLUSTER_NAME)
-DcassandraKeyspaceName : Cassandra keyspace name (optional, default value is "mangledb" if you are using the Cassandra image file from Mangle Docker repo)
-DcassandraPorts : Cassandra DB port used (optional, default value is "9042")
-DcassandraSslEnabled : Cassandra DB ssl configuration (optional, default value is "false"...mandatory and should be set to true if ssl is enabled)
-DcassandraUsername : Cassandra DB username (mandatory only if the Cassandra DB is created with authentication enabled)
-DcassandraPassword : Cassandra DB password (mandatory only if the Cassandra DB is created with authentication enabled)
-DcassandraSchemaAction : Cassandra DB schema action (optional, default value is "create_if_not_exists")
-DcassandraConsistencyLevel : Cassandra DB Consistency level (optional, default value is "local-quorum")
-DcassandraSerialConsistencyLevel : Cassandra DB serial consistency level (optional, default value is "local-serial")
-DcassandraDCName : Cassandra DB DC name (optional, value should be the one passed during cassandra db creation as an environmental variable CASSANDRA_DC or if it is set a value other than "DC1")
-DcassandraNoOfReplicas : Cassandra DB replicas numbers (optional, default value is "1"...mandatory only if multiple nodes are available in the DB cluster)
```

**Supported CLUSTER\_OPTIONS**

```text
-DpublicAddress : IP address of the mangle node (mandatory)
-DclusterValidationToken : Any string token name for mangle cluster (mandatory and should be kept in mind if more nodes need to be added to the cluster.)
-DclusterName : Any string cluster name for mangle (optional, default value is "mangle")
-DclusterMembers : Members in the mangle cluster (optional)
```

#### **Deploying the Mangle vCenter adapter container**

Mangle vCenter Adapter is a fault injection adapter for injecting vCenter specific faults. All the vCenter operations from the Mangle application will be carried out through this adapter.

To deploy the vCenter adapter container run the docker command below on the docker host.

```text
docker run --name mangle-vc-adapter -v /var/opt/mangle-vc-adapter-tomcat/logs:/var/opt/mangle-vc-adapter-tomcat/logs -d -p 8080:8080 -p 8443:8443 mangleuser/mangle_vcenter_adapter:<VERSION>
```

{% hint style="info" %}
The API documentation for the vCenter Adapter can be found at: 

_https://&lt;NODE-IP&gt;:&lt;PORT&gt;:/mangle-vc-adapter/swagger-ui.html_

The vCenter adapter requires authentication against any API calls. It supports only one user, _admin_ with password _admin_. All the post APIs that are supported by the adapter will take the vCenter information as a request body.
{% endhint %}

### Deploying Mangle on Kubernetes

All the relevant YAML files are available under the Mangle git repository under location 'mangle\mangle-support\kubernetes'

1. Create a namespace called 'mangle' under the K8s cluster.

   `kubectl --kubeconfig=`_`<kubeconfig file>`_ `create namespace mangle`

2. Create Cassandra pod and service.

   `kubectl --kubeconfig=`_`<kubeconfig file>`_ `-n mangle apply -f` _`<path>`_`/cassandra.yaml`

   `kubectl --kubeconfig=`_`<kubeconfig file>`_ `-n mangle apply -f` _`<path>`_`/cassandra-service.yaml`

3. Create Mangle pod and service

   `kubectl --kubeconfig=`_`<kubeconfig file>`_ `-n mangle apply -f` _`<path>`_`/mangle.yaml`

   `kubectl --kubeconfig=`_`<kubeconfig file>`_ `-n mangle apply -f` _`<path>`_`/mangle-service.yaml`

## Multi Node Deployment

A multi-node setup for Mangle ensures availability in case of unexpected failures. We recommend that you use a 3 node Mangle setup. 

#### Prerequisites

You need at least 4 docker hosts for setting up a multi node Mangle instance; 1 for the Cassandra DB and 3 for Mangle application containers . You can setup a docker host by following the instructions [here](https://docs.docker.com/install/).

A multi node setup of Mangle is implemented using Hazelcast. Mangle multi node setup uses TCP connection to communicate with each other. The configuration of the setup is handled by providing the right arguments to the docker container run command, which identifies the cluster.

The docker container takes an environmental variable "**CLUSTER\_OPTIONS**", which can take a list of java arguments identifying the properties of the cluster. Following are the different arguments that should be part of "CLUSTER\_OPTIONS":  
  
**clusterName** - A unique string that identifies the cluster to which the current mangle app will be joining. If not provided, the Mangle app will by default use string "mangle" as the clusterName, and if this doesn't match the one already configured with the cluster, the node is trying to join to, container start fails.  
  
**clusterValidationToken** - A unique string which will act similar to a password for a member to get validated against the existing cluster. If the validation token doesn't match with the one that is being used by the cluster, the container will fail to start.

**publicAddress** - IP of the docker host on which the mangle application will be deployed. This is the IP that mangle will use to establish a connection with the other members that are already part of the cluster, and hence it is necessary to provide the host IP and make sure the docker host is discoverable from other nodes  
  
**clusterMembers** - This is an optional property that takes a comma-separated list of IP addresses that are part of the cluster. If not provided, Mangle will query DB and find the members of the cluster that is using the DB and will try connecting to that automatically. It is enough for mangle to connect to at least one member to become part of the cluster.

**deploymentMode** - Takes either CLUSTER/STANDALONE value. Deployment Mode parameter is mandatory for the multi-node deployment, with the value set to CLUSTER on every node that will be part of the cluster, where as for the single node deployment, this field can be ignored, which by default will be STANDALONE. If DB was used by one type of deployment setup, one needs to update this parameter to support the other type, either though the mangle or by directly going in to DB.

{% hint style="info" %}
**NOTE:** 

All the nodes \(docker hosts\) participating in the cluster should be synchronized with a single time server.

If a different mangle app uses the same clusterValidationToken, clusterName and DB of existing cluster, the node will automatically joins that existing cluster.

All the mangle app participating in the cluster should use the same cassandra DB. 

The properties clusterValidationToken and publicAddress are mandatory for any mangle container spin up, if not provided container will fail to start. 
{% endhint %}

Deploy a Cassandra DB container by referring to the section [here](./#prerequisites). 

Deploy the Mangle cluster by bringing up the mangle container in each docker host.

**For the first node in the cluster:**

```text
docker run --name mangle -d -v /var/opt/mangle-tomcat/logs:/var/opt/mangle-tomcat/logs -e DB_OPTIONS="-DcassandraContactPoints=<Cassandra-IP>" -e CLUSTER_OPTIONS="-DclusterName=<CLUSTER-NAME> -DclusterValidationToken=<CLUSTER-VALIDATION-TOKEN> -DpublicAddress=<DOCKER-HOST-IP-1> -DdeploymentMode=CLUSTER" -p 8080:8080 -p 443:8443 -p 5701:5701 mangleuser/mangle:<VERSION>
```

**For the subsequent nodes in the cluster:**

```text
docker run --name mangle -d -v /var/opt/mangle-tomcat/logs:/var/opt/mangle-tomcat/logs -e DB_OPTIONS="-DcassandraContactPoints=<Cassandra-IP>" -e CLUSTER_OPTIONS="-DclusterName=<CLUSTER-NAME> -DclusterValidationToken=<CLUSTER-VALIDATION-TOKEN> -DpublicAddress=<DOCKER-HOST-IP-2> -DclusterMembers=<DOCKER-HOST-IP-1> -DdeploymentMode=CLUSTER" -p 8080:8080 -p 443:8443 -p 5701:5701 mangleuser/mangle:<VERSION>
```

```text
docker run --name mangle -d -v /var/opt/mangle-tomcat/logs:/var/opt/mangle-tomcat/logs -e DB_OPTIONS="-DcassandraContactPoints=<Cassandra-IP>" -e CLUSTER_OPTIONS="-DclusterName=<CLUSTER-NAME> -DclusterValidationToken=<CLUSTER-VALIDATION-TOKEN> -DpublicAddress=<DOCKER-HOST-IP-3> -DclusterMembers=<DOCKER-HOST-IP-1, DOCKER-HOST-IP-2> -DdeploymentMode=CLUSTER" -p 8080:8080 -p 443:8443 -p 5701:5701 mangleuser/mangle:<VERSION>
```

## Deployment Mode and Quorum

Mangle implements quorum strategy to for supporting HA and to avoid any split brain scenarios when deployed as a cluster.

Mangle can be deployed in two different deployment modes

1. **STANDALONE**: quorum value is 1, cannot be updated to any other value
2. **CLUSTER**: min quorum value is 2, cannot be set to value lesser than ceil\(\(n + 1\)/2\), n being number of nodes currently in the cluster

If a node is deployed initially in the STANDALONE mode, and user changes the deployment mode to CLUSTER, the quorum will automatically be updated to 2. When user keeps adding new nodes to the cluster, the quorum value updates depending on the number of nodes in the cluster.  
eg: when user adds new node to 3 node cluster\(quorum=2\) making it 4 nodes in the cluster, the quorum value is updated to 3. Determined by the formula, ceil\(\(n + 1\)/2\)

If a node is deployed initially in the CLUSTER mode, and user changes the deployment mode to STANDALONE mode, all other nodes except for the Hazelcast master will shutdown. They will lose the connection with the existing node, and won't take any post calls, and will entries will be removed from the cluster table.

{% hint style="info" %}
**NOTE:** 

Active members list of the active quorum will be maintained in DB under the table cluster. Similarly, master instance entry will also be maintained in the db under the same table.
{% endhint %}

### **Actions triggered when Mangle loses quorum**

1. All the schedules will be paused
2. No post calls can be made against mangle
3. Task that were triggered on any node will continue to be executed until Failed/Completed
4. Removes its entry from the cluster table's members list
5. If the node was the last known master, it will remove it's entry as master from the table

### **Actions triggered when Mangle gets a quorum \(from no quorum in any of the cluster member\)**

1. All the schedules that are in the scheduled state and all the schedules that were paused because of the quorum failure will be triggered
2. All the tasks that are in progress will be queue to executed after 5minutes of quorum created event \(This is to avoid any simultaneous Execution\)
3. Master \(Oldest member in the cluster\) will take care of adding the list of active members to the cluster table, and updating itself as master

### **Actions triggered when a Mangle instance joins one active cluster**

1. Some of the schedules will be assigned to the new the node due to migration
2. Some of the tasks will assigned to the new node, and they will be queued for triggering after 5mins
3. Existing tasks that in-progress will continue to execute in the old node
4. Master \(Oldest member in the cluster\) will take care of adding new member's entry to the list of active members to the cluster table

### **Scenarios around quorum**

#### **Scenario 1:** When a mangle instance in 3 node cluster, leaves a cluster due to network partition, and the quorum value is 2

* Let us consider 3 instances a, b, c in the CLUSTER
* a leaves the cluster due to network partition
* a is not able to communicate with b, c
* two cluster will be created, one having only a, and the other have b and c
* since for the cluster having a doesn't have quorum\(2\), it loses the quorum, and removes itself as active member from cluster config
* cluster having nodes b and c continue execute since they have enough nodes to satisfy the quorum

#### Scenario 2: When mangle instance in 2 node cluster leaves a cluster due to network partition, and the quorum value is 2

* Let us consider 2 instances a, b in the CLUSTER
* a leaves the cluster due to network partition, a cannot communicate with b and vice versa
* two clusters are created with each having one node
* both the cluster loses the quorum\(2\)

