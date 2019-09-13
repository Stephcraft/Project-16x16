Contributing to Project-16x16
=============================

Project-16x16 is an open source project and we'd love to receive your contribution. There are many ways to contribute: improving documentation (the wiki), submitting bug reports and feature requests, or writing code which can be incorporated into the game itself.

## Contributing code and documentation changes
### Fork and clone the repository
Decided you want to contribute? First fork the repository! To do this, press on the `Fork` button at the top right corner. This will clone the project into your GitHub account. See the [GitHub help page](https://help.github.com/articles/fork-a-repo) for help.
### Setting up the project
You've cloned the repository into your Github account -- now what? 

You'll need to **download** the project to your machine. From there, create a Java project from the source files in your favourite IDE (it is possible to develop in any major Java IDE: Eclipse; IntelliJ; NetBeans).

#### How do I download?
Use git or a git-based application -- we recommend [SourceTree](https://www.sourcetreeapp.com), a free git GUI client.

#### Eclipse Guide
*In this guide, we're assuming you've saved the project at `C:\Users\%USER%\Desktop\Project-16x16` (of course it doesn't need to be this)*...

1. Create a new, empty project [File>>New>>Java Project...] -- set the JRE to Java 8+ and click `Finish`.
2. Import the *project-16x16* files from the file system into the empty project.
    * [File>>Import>>General->File System].
    * In the `File System` dialog, set the `From directory` to the `source` folder in the project folder where you downloaded the project files (example: `C:\Users\Mike\Desktop\Project-16x16\source`).
    * The folders will now appear in eclipse. Select `Assets`, `libraries` and `src` **individually** -- **do not select them all at once by selecting `source`**.
    * Set `Into Folder` to the project you just created and click `Finish`.
3. Now add the required libraries to the project.
    * [Project>>Right-Click>>Build Path>>Configure Build Path...>>Libraries Tab].
    * Under classpath, click `Add JARs` and select all the `.jars` in the `libraries` folder.
    * `Apply and Close` the  *Build Path* dialog.

#### Java Project Requirements
* **JDK 8** is required to build the game, however we recommend using JDK 10 or above.
* **JavaFX 12** (at a minimum) (see [openjfx](https://openjfx.io]). This can be found in the [`libraries`](https://github.com/Stephcraft/Project-16x16/tree/development/source/libraries) folder in the repo.
* **Other Libraries**: these can also all be found in the [`libraries`](https://github.com/Stephcraft/Project-16x16/tree/development/source/libraries) folder. Add to classpath of your IDE's Java project.


### Submitting your changes via a pull request
A pull request is the final step to contribute something. A request should be made when you complete a "milestone" or resolve an issue.

1. Test your changes

    Does the game still work?
  
2. Rebase your changes

    Update your local repository with the most recent code from the main [repository]((https://github.com/Stephcraft/Project-16x16)), and rebase your branch on top of that. This means that *you* sort out any possible [merge conflicts](https://help.github.com/en/articles/about-merge-conflicts) so that pull request will be conflict-free.

3. Submit a pull request

    Push your local changes to your forked copy of the repository and [submit a pull request](https://help.github.com/articles/using-pull-requests). In the pull request, choose a title which sums up the changes that you have made, and in the body provide more details about what your changes do.

Then, sit back and wait. There will might be discussion about the pull request and, if any changes are needed, we would love to work with you to get your pull request merged into the game.