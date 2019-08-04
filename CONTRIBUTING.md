# Contribute

#### Setup
1. Download git [here](https://git-scm.com/downloads). Alternatively, use a GUI git client such as SourceTree.
2. Fork this repository. Press on the Fork button at the top right corner. This will clone it to your GitHub account.
3. Open CMD/command prompt if on Windows or Terminal if on mac
4. Now, let's clone your fork. In command prompt, type ```cd directory/to/where/you/want/your/project``` for example: ```cd Desktop/Programming/Java```
5. Execute ```git clone https://github.com/your-github-username/Project-16x16/``` example url: ```https://github.com/Noodleman123/Project-16x16/```
6. Execute ```cd Project-16x16``` to go in the project directory
7. Execute ```git checkout development``` to have the right files, the ones that are being developed.

#### Project IDE Guidelines
You'll have to create a java project in an IDE using the files you've downloaded through git.

Once you've imported the source files into a project in your IDE, add all .jars from the source/libraries/ folder to the project classpath. Now, **set the project runtime to** [**Java 12**](https://jdk.java.net/12/), because some JavaFX funcionality requires JavaFX 12 (and that requiries the Java 12 runtime).

#### Save on your local version control aka git
You should execute these 2 commands everytime you complete a task or something important:
```
git add .
git commit -m "description telling what you modified/added"
```

#### Save in the cloud aka GitHub
You should save your local work on your forked GitHub when you accomplish something important.
```
git push development
```

#### Pull request
A pull request is the final step to contribute something. A request should be made when you complete a "milestone" or resolve an issue.
1. Go to the project 16x16 GitHub repository and click on Pull requests
2. Click on the big green Pull request button
3. Be sure to choose the right repository and the correct branch which is `development`

## Tasks
Now you are ready to contribute! Tasks are represented as issues in GitHub. You can find them in the **Issues** tab of the repository or by using [this](https://github.com/Stephcraft/Project-16x16/issues) link. You can also add issues if you find some or if you want to contribute to one that is not listed.
