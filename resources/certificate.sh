#!/bin/sh

cd `dirname ${0}` && java -jar certificate.jar "$@"
cd -
