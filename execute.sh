#!/bin/bash
jar cfm lottery.jar manifest.txt -C bin com
java -jar lottery.jar
