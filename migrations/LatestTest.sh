#!/bin/bash

echo ""
echo "Applying migration LatestTest"

echo "Adding routes to conf/app.routes"
echo "" >> ../conf/app.routes
echo "GET        /latestTest                       uk.gov.hmrc.automatedexportsystemfrontend.controllers.LatestTestController.onPageLoad()" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "latestTest.title = latestTest" >> ../conf/messages.en
echo "latestTest.heading = latestTest" >> ../conf/messages.en

echo "Migration LatestTest completed"
