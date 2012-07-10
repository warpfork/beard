Beard
=====

Beard conceals a sleek java core under a scruffy guise of an HTML5/CSS3/JS web-2.whatever app.

Applying this tool results in powerful client-side computation with full local resource bundles that can be used to build processing-intensive rapid-response goodness.



Compiling
---------

Ant.



Using as a library
------------------

To compile other projects using Beard as a library, you need only add `beard.jar` to the build path.

To run other projects using Beard as a library:
* `beard.jar` must be on the classpath.
* `lib/ahslib-core.jar` must be on the classpath.
* `lib/ahslib-thread.jar` must be on the classpath.
* files within `beard.jar:/res/` must be remain accessible as resources.

Note that some files are not required when using Beard (they're only required to build it):
* `lib/plugin.jar` is NOT required to be on the classpath.

If you're building a project that deploys as an applet (and of course you are), you might be interested in being able to ship your entire product as a single jar.
If you use Ant as a build system, there are a number of handy tasks that will do exactly what you want in order to repack all this library stuff into your product jar while leaving resource paths all intact.
Here's an example:

	<jar jarfile="${dist}/yourapp.jar" basedir="${build}/main">
		* ... whatever other parameters your packaging involves ... *
		<zipfileset src="${lib}/beard.jar" includes="**/*.class res/**"/>
	</jar>

That `zipfileset` line automagically shifts all the compiled code and the resources files from the Beard jar into your product's jar.



Other tips for Applet developers
================================

Dealing with Class Caching in Browsers
--------------------------------------

If you haven't realized it already, you probably soon will: Dealing with class caching in browsers is a huge pain in the ass.  F5/refresh doesn't always actually give you the freshest version of your program!

Fortunately, there's a way trick your browser into loading your applet completely fresh every time you do a code deploy, and it's pretty easy to automate with Ant.

Wedge something like these lines in whatever target you use for local builds (for me it's typically the "dist" target, and this snippet belongs right after the `<jar>` part of the build):

	<tstamp><format property="timemark" pattern="yyyyMMddHHmmssSSS"/></tstamp>
	<symlink resource="yourapp.jar" link="${dist}/yourapp-${timemark}.jar"/>
	<delete file="${dist}/index.html"/>
	<copy tofile="${dist}/index.html" file="${pre}/index.html.tmpl"/>
	<replace file="${dist}/index.html">
		<replacefilter
			token="@VERSION@"
			value="-${timemark}"/>
	</replace>

Meanwhile, your `index.html.tmpl` template file contains something like this:

	<object type="application/x-java-applet">
		<param name="code" value="com.example.yourapp.Applet">
		<param name="archive" value="yourapp@VERSION@.jar">
		<param name="codebase" value=".">
		<param name="scriptable" value="true">
		<param name="mayscript" value="true">
	</object>

Basically we're making a web page that refers to the applet jar with a timestamp in its filename,
then making a symlink with the same timestamp in the name to the actual jar file
(which means the jar file itself always gets built to the same location,
so if you do the whole thing repeatedly without calling any kind of cleanup step on your dist directory you'll end up with extra simlinks but
incremental building itself will still work correctly and you won't end up with tonnes of useless copies of your jar taking up disk space).
This only works on machines that support symlinks of course, so if you're using Windows, go to hell.

When doing releases to production, you'll probably do some filename trick like this as well to make sure all your users don't get stuck with old programs in their browser cache.


Continuous Development with Automatic Incremental Builds
--------------------------------------------------------

Another protip: running `watch ant` (on a linux/GNU or similar machine of course) will result in incremental builds of your project occuring automatically every few seconds!
This means you can make changes in your code editor, then alt-tab to your browser, F5, and huzzah!  Your changes already show up, and you never had to switch out to a terminal to run the build process.


Fun Facts about Applet Threading Models
---------------------------------------

You're probably familiar with the four methods defining an applet's lifecycle:
```init()```, ```start()```, ```stop()```, and ```destroy()```
(if you're not familar, here's the tutorial from the java site: http://docs.oracle.com/javase/tutorial/deployment/applet/lifeCycle.html ).

All of these methods are called from the same thread.

You may want to sing "la la la I don't hear you" as much as possible in your application development, and that's fine;
Beard will try to support you as much as possible along that path.
However, this one-thread-for-all-lifecycle-events has one major impact that you should be aware of to it doesn't scare the bejesus out of you later.

If you don't return from the start method, you'll never get a stop or a destroy event.
If you don't return from the init method, you'll never even get a call to the start method, much less the other two.
This in turn means that your code will be ended by the thrown a violent ThreadDeath.
This is bad, and will almost certainly cause more bad.
Whatever your code is doing will be interrupted; if you're doing any calls to ```Object.wait()```, they will be disrupted;
any monitors in any other threads are left inconsistent state, the works.
Basically your applet will crash and the entire vm will probably die.

So return from the start method.  And all the lifecycle methods.

Start a thread somewhere.  Do your work in that.

Make sure it's stopped by the destroy event.

And then you're safe and stable!  (And you can still pretty much treat that one thread you started as the your one-and-only precious and dodge all further concurrency issues, if that's how you like to roll.)

(And do NOT use WorkManager.getDefaultScheduler() in an Applet.  (Or any other static thread pool, for that matter.)
If you stop it, you're messing things up for other applet instances that may run in the same JVM; if you don't stop it, you're crashing.
It's a no-win situation.
Make your own WorkScheduler per Applet.)

