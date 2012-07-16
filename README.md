
maven-continous-test-plugin
===========================

plugin for maven to continously run tests

THIS IS WORK IN PROGRESS!!!


The Idea 
--------

I recently discovered [sbt](https://github.com/harrah/xsbt/) for Scala and loved the continous execution it provides. This continous execution is invoked prepending '~' to any command you pass to sbt (more or less, I don't want to duplicate documentation). The actual behaviour is not that it will execute endlessly but everytime it has to (a file is changed, for example).

Since then I'm missing that in Maven so I decided to give it a try.


Status
------

I can now run the tests over a given module (the target module needs to declare the usage of the continous test plugin). But this number of executions is hardcoded and is currently launched sequentially and all at once. No wait for any event of any kind.

Usage
-----

* make sure you use Maven 3
* for this repo and run a maven clean install to get the plugin locally (no release yet, sorry)

<pre><code>  git clone git@github.com:ignasi35/maven-continous-test-plugin.git
  mvn clean install  
</code></pre>

* Add the plugin to the module you want to continously test:

<pre><code>	&lt;build&gt;
		&lt;plugins&gt;
			&lt;plugin&gt;
				&lt;groupId&gt;com.marimon.maven&lt;/groupId&gt;
				&lt;artifactId&gt;continous-test-maven-plugin&lt;/artifactId&gt;
				&lt;version&gt;${project.version}&lt;/version&gt;
				&lt;executions&gt;
					&lt;execution&gt;
						&lt;phase&gt;test&lt;/phase&gt;
						&lt;goals&gt;
							&lt;goal&gt;retest&lt;/goal&gt;
						&lt;/goals&gt;
					&lt;/execution&gt;
				&lt;/executions&gt;
			&lt;/plugin&gt;
		&lt;/plugins&gt;
	&lt;/build&gt;
</code></pre>


Todo list
---------

The fllowing is incomplete but is more or less prioritized from higher to lower priority.

*TODO*: enter an infinite loop until a certain key is pressed.

*TODO*: provide colored feedback so it's easie to see if tests passed or failed.

*TODO*: move this *TODO*s into GitHub Issues.

*TODO*: detect file change (use sbt's approach of scanning folder every 200ms)

*TODO*: launch recompilation of the parts that changed

*TODO*: use Java7 (if available to detect file change)

*TODO*: make the plugin be declared in a parent pom so it's usable in all modules

*TODO*: detect changes in dependency modules soanged module is recompiled (and current module is relaunched? and both are relaunched? and all dependency chain is relaunched?).
