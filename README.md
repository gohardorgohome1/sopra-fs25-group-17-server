# User guide for adding and analyzing an exoplanet

The goal of this section is to guide users through the process of adding and analyzing exoplanets within the application. While the user interface becomes fairly intuitive after reading the sections below—which explains our project and the UI functionalities—we believe the specific step of adding and analyzing exoplanets might still feel slightly unintuitive. This is mainly because the user needs to upload a photometric light curve. Here's a step-by-step explanation of the process:

1. **Download the `.txt` files (photometric light curves) from the Client GitHub**

   In the Client GitHub repository, we have provided five photometric light curves. These files are in `.txt` format and can be easily identified. Each file is named after the exoplanet it corresponds to.

2. **Register / Log in and navigate to the Dashboard**

3. **Click on "Add and Analyze Exoplanet"**

   On the Dashboard, the user should click on "Add and Analyze Exoplanet". This will redirect them to the page where photometric curves can be uploaded.

4. **Click on "Upload File"**

   Here, the user needs to upload one of the `.txt` files mentioned in Step 1.

5. **Enter the exoplanet name in the text box**

   To simplify the process, the name of the exoplanet should exactly match the name of the uploaded `.txt` file.  
   For example, if the file is named `TrES-3 b.txt`, the user must enter `TrES-3 b` in the input field.  
   It is very important to respect capitalization and spacing. We understand that this requirement increases the chances of user error. This will be improved in Milestone 4.

6. **Enter the name of the star**

   Although we are not yet using this field for queries, it still needs to be filled in. It can be made up without affecting the analysis.  
   However, for accuracy, the name of the star is usually the exoplanet name without its last letter.  
   For instance, the star corresponding to `TrES-3 b` would be `TrES-3`.

7. **Click on "Calculate"**

   This will generate a new profile for the exoplanet and automatically redirect the user to its dedicated page. The exoplanet will also be added to the Dashboard and a real-time notification will be sent to all users. From this point on, the user can continue using the platform as normal.


# Project Objective and Scientific Background
In this project, we aim to develop a tool that can be useful for the automation and optimization of the search for habitable exoplanets. Specifically, our project focuses on the analysis of planetary transits. 

A planetary transit is a very useful and widely used method in astrophysics to search for exoplanets in the universe. A transit occurs when an exoplanet passes in front of its star from the Earth's frame of reference. Figure 1 is an example of a planetary transit. 

Astrophysicists and space telescopes measure the light coming from a certain star, and if a planetary transit occurs, we will observe that the light from that star drops over a period of time, forming a curve. This curve is called a photometric curve, and it is the input provided by the user on our platform. By calculating the percentage drop in brightness from this curve, and combining it with some star data retrieved via the NASA Exoplanet Archive TAP API, we can compute key parameters of the exoplanet such as its mass, radius, density, surface temperature, gravity, etc.

One particularly important parameter we calculate is the Earth Similarity Index, which tells us how similar the exoplanet is to Earth. This allows us to identify potentially habitable planets. 

To summarize, this is the physical and scientific core of our project, which helps explain what our platform does and how it can assist in the search for habitable exoplanets. 

# The platform: UI and functionalities

In this section we describe the platform itself and the user interface.

First, the user – typically an astrophysicist or a researcher – must register or log in to access the system. Once authenticated, the user lands on the Dashboard, the main page of the platform. On the left side of the Dashboard, there's a plot (with Orbital Period on the x-axis and Radius on the y-axis) that classifies exoplanets analyzed by all users into categories: Earth-Like Planets, Rocky Planets, Lava Worlds, Ocean Worlds, Ice Giants, Cold Gas Giants, and Hot Jupiters. This graph is very helpful for quickly identifying which planets might harbor life and which ones probably don't. Since clusters are formed in this plot, a researcher might want to further investigate a particular group of exoplanets.

On the right side of the Dashboard, the user can also see an Earth Similarity Ranking. This ranks the exoplanets analyzed by all users based on their Earth Similarity Index, a metric that tells us how Earth-like a given planet is and thus how likely it is to support life. In this way, researchers can narrow the search and focus on the most promising candidates. 

Users can also analyze a new exoplanet by clicking the “Analyze and Add Exoplanet” button on the Dashboard. This button redirects the user to a page where they can upload a photometric light curve. In practice, astrophysicists obtain these curves by taking telescope images of a star during a planetary transit. After postprocessing the images, they extract the photometric curves for each exoplanet. To simplify this process, we’ve used curves from the Exoplanet Transit Database. These are simply .txt files containing metadata and information about the filters used for the observations, as well as numerical data (magnitude and time) that generate the light curve. In our client GitHub repo, we’ve included several .txt files named after their corresponding exoplanets so the TAs can test our platform. 

So, to analyze an exoplanet, the user must upload a .txt file containing a photometric curve and fill in the corresponding fields with the name of the exoplanet and its star. It’s important to emphasize that, in this milestone, the exoplanet name must be entered correctly because it’s used to query the NASA Exoplanet Archive TAP API — if the name is incorrect, the external API won’t return any data. For Milestone 3, the name of the star is not yet relevant — only the exoplanet name is required. 

Once the user clicks “Calculate,” a profile page is automatically generated for that exoplanet. First, the photometric curve is displayed (time on the X-axis, brightness on the Y-axis), showing the light dip caused by the exoplanet passing in front of its star. The page also displays all the calculated parameters of the exoplanet, based on formulas and the data retrieved from the NASA API (we fetch the star’s radius, the planet’s orbital period, and its temperature). Notably, the units used for all magnitudes are relative to Earth — for instance, the planet’s mass is expressed in Earth masses, its radius in Earth radii, etc. Lastly, the page includes a comment section, where users can post real-time, persistent feedback about the exoplanet. 

As soon as the user clicks “Calculate” and the exoplanet profile page is generated, a real-time notification (via WebSockets) is sent to all users in the Dashboard, indicating that a new exoplanet has been added. The planet also appears instantly in both the classification plot and the Earth Similarity Ranking. In both places, users can click on the exoplanet and will be redirected to its profile page. Additionally, users can delete an exoplanet — but only if they have added it — using the “Delete Exoplanet” button. 

Lastly, we’ve also implemented an AI Assistant to answer questions and provide information about exoplanets. To access it, users can click “AI Assistant” in the Dashboard. This opens a chat interface, similar to OpenAI’s ChatGPT. However, in our case, the conversation is public — all users can see the questions asked by others and the responses from the AI Assistant. We’ve done this to encourage collaboration and shared knowledge in the fields of astrophysics and astrobiology. The AI Assistant uses OpenAI’s Chat Completions API with the gpt-4o-mini model. 

In conclusion, the goal of our project is to contribute to the progress of planetary detection and habitability research, while fostering collaboration among researchers. As someone wise once said: "Alone you go fast, but together you go far."


# SoPra RESTful Service Template FS25

## Getting started with Spring Boot
-   Documentation: https://docs.spring.io/spring-boot/docs/current/reference/html/index.html
-   Guides: http://spring.io/guides
    -   Building a RESTful Web Service: http://spring.io/guides/gs/rest-service/
    -   Building REST services with Spring: https://spring.io/guides/tutorials/rest/

## Setup this Template with your IDE of choice
Download your IDE of choice (e.g., [IntelliJ](https://www.jetbrains.com/idea/download/), [Visual Studio Code](https://code.visualstudio.com/), or [Eclipse](http://www.eclipse.org/downloads/)). Make sure Java 17 is installed on your system (for Windows, please make sure your `JAVA_HOME` environment variable is set to the correct version of Java).

### IntelliJ
If you consider to use IntelliJ as your IDE of choice, you can make use of your free educational license [here](https://www.jetbrains.com/community/education/#students).
1. File -> Open... -> SoPra server template
2. Accept to import the project as a `gradle project`
3. To build right click the `build.gradle` file and choose `Run Build`

### VS Code
The following extensions can help you get started more easily:
-   `vmware.vscode-spring-boot`
-   `vscjava.vscode-spring-initializr`
-   `vscjava.vscode-spring-boot-dashboard`
-   `vscjava.vscode-java-pack`

**Note:** You'll need to build the project first with Gradle, just click on the `build` command in the _Gradle Tasks_ extension. Then check the _Spring Boot Dashboard_ extension if it already shows `soprafs24` and hit the play button to start the server. If it doesn't show up, restart VS Code and check again.

## Building with Gradle
You can use the local Gradle Wrapper to build the application.
-   macOS: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew bootRun
```

You can verify that the server is running by visiting `localhost:8080` in your browser.

### Test

```bash
./gradlew test
```

### Development Mode
You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

## API Endpoint Testing with Postman
We recommend using [Postman](https://www.getpostman.com) to test your API Endpoints.

## Debugging
If something is not working and/or you don't know what is going on. We recommend using a debugger and step-through the process step-by-step.

To configure a debugger for SpringBoot's Tomcat servlet (i.e. the process you start with `./gradlew bootRun` command), do the following:

1. Open Tab: **Run**/Edit Configurations
2. Add a new Remote Configuration and name it properly
3. Start the Server in Debug mode: `./gradlew bootRun --debug-jvm`
4. Press `Shift + F9` or the use **Run**/Debug "Name of your task"
5. Set breakpoints in the application where you need it
6. Step through the process one step at a time

## Testing
Have a look here: https://www.baeldung.com/spring-boot-testing
