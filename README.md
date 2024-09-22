<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>

<!-- GETTING STARTED -->
## Getting Started

This is an example of how you may give instructions on setting up your project locally.
To get a local copy up and running follow these simple example steps.

### Prerequisites

You should use this command to load modules to your machine
* git
  ```sh
  git clone --recurse-submodules git@git.toprate.io:mytel/mytel-family/mytel-family-internal.git
  #git submodule update --init --recursive
  git submodule update --recursive --remote
  
  #git submodule foreach --recursive git checkout main
  git submodule foreach --recursive "git checkout main || true"
  ```
<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- DESCRIPTION -->
## Description to develop
This module has 4 packages:


1. business: contains all of features in the system
- in: implement business partners use mytel as their third party
- out: implement business mytel use partners (bank/wallet) as our third party system
2. controller: write the api controller. Require seprate for partner controller and mobile controller 
3. exception: handler all of exceptions
4. errorCode: please add more error code & the message flow each object (account, transaction, partner)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

