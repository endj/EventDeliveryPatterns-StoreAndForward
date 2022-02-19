#!/bin/sh

printUsage() {
    bold=$(tput bold)
    normal=$(tput sgr0)
    echo "

    ${bold}BoilerPlate Generator 0.1${normal}

    ${bold}---- Required parameters ----${normal}

     -g <groupId>
     -a <artifactId>
     -s <serviceName>

    ${bold}---- Optional parameters ----${normal}

     -j <pathToJsonFile>


    ${bold}---- Property Explanation ----${normal}

    ${bold}[ -j ]${normal}

    JSON domain model file property should point to a file containing a JSON payload that
    represents some domain object such as a Person and what types fields have. If a JSON model is provided,
    a CRUD REST based API will be generated using the JSON model.


    ${bold}NOTE!${normal}

    If no JSON definition file is provided, the application will generate code defined
    in code-generation/defaultModel.json

    ${bold}---- Example usage ----${normal}


    Generate service with HTTP API and a specific domain model

    ./generate-project.sh -g se.edinjakupovic \\
                       -a cool-service \\
                       -s cool-serviceName \\
                       -j ./employee.json

    Generate a service with default model

    ./generate-project.sh -g se.edinjakupovic \\
                       -a cool-service \\
                       -s cool-serviceName
    "
}

groupId=""
artifactId=""
serviceName=""
domainJsonDefinitionFile=""

while getopts g:a:s:j:h o; do
  case $o in
    (g) groupId=$OPTARG;;
    (a) artifactId=$OPTARG;;
    (s) serviceName=$OPTARG;;
    (j) domainJsonDefinitionFile=$OPTARG;;
    (h) printUsage;;
    (*) echo "Unrecognized argument ${OPTARG}"
  esac
done

if [ -z "$groupId" ] ; then
        echo 'Missing required argument for groupId -g' >&2
        exit 1
fi

if [ -z "$artifactId" ] ; then
        echo 'Missing required argument for artifactId -a' >&2
        exit 1
fi

if [ -z "$serviceName" ] ; then
        echo 'Missing required argument for serviceName -s' >&2
        exit 1
fi

if [ -n "$domainJsonDefinitionFile" ] && ! [ -e "$domainJsonDefinitionFile" ] ; then
        echo "Could not find file for domain definition given path: ${domainJsonDefinitionFile}"
        exit 1
fi

if [ -n "$domainJsonDefinitionFile" ] ; then
  cp "${domainJsonDefinitionFile}" code-generation/ || (echo "failed to copy jsonDefinition" && exit 1)
fi

cd code-generation || (echo "Unable to navigate to code-gen folder" && exit 1)

javac BoilerPlateGenerator.java

java BoilerPlateGenerator \
     groupId="${groupId}" \
     artifactId="${artifactId}" \
     serviceName="${serviceName}" \
     domainJsonDefinitionFile="${domainJsonDefinitionFile}"

rm ./*.class

cd ..
rm -r code-generation
rm README.md
echo "## ${serviceName}" > README.md
echo "Run \`mvn clean install && java -jar target/*.jar\`" >> README.md
rm generate-project.sh
mvn clean install && java -jar target/*.jar
