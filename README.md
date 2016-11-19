
FIRST BUILD GENERAL
    If you are not using intellij, PLEASE PLEASE UPDATE THE GITIGNORE FILE TO IGNORE YOUR IDE PROJECT ORGANIZATION
FILES! Currently, we have no build instructions for IDEs other than IntelliJ, so please add a section here if you figure
it out.

FIRST BUILD INSTRUCTIONS FOR INTELLIJ
    Building this the first time takes a little setup because we import a few .jar and .dll files. In order to run it
the first time, you'll need to do a few things:
    a) structure the project:
Firstly, you don't have to import the project a la Eclipse... just open the project folder with IntelliJ. One of the top
right icons of the IDE is called "structure project." There, you'll want to configure your jdk (at least 1.8), and set 
an out folder for the binary (this can be anywhere).
Next, you'll want to import libraries. To do this, go to the libraries
tab and add our libraries (lib/jar/{{add each separately}}). For the LWJGL lib to work, you'll also have to add the
lib/native directory to the LWJGL module you've created using the smaller '+' button on the right hand side.
Finally, you will need to setup the project modules on the modules. On the right, select the Source tab and mark the src
folder as sources and the out folder you set earlier as excluded. Under the Paths tab make sure the option to Inherit 
project output compile path.
    b) configure a build:
Somewhere to the left of the structure project icon is a dropdown for picking and editing builds. Click edit
configurations and create a new application build (select application on the left). Under the configurations tab, use
the ellipsis box on the right of the "Main class" field to select Driver.Main as the main file. If it does not come up
as an option, then the project structure was not setup properly; ensure that the src folder has been marked as
'sources.' Once that has been set, make sure the "Working directory" is the full directory of the project (the one above
lib, src, and assets). Lastly, set "Use classpath of module" to be that same folder.

At this point, if you hit run/build, the project should compile and run.