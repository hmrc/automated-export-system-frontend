#!/bin/bash

echo ""
echo "Applying migration $className;format="snake"$"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /$className;format="decap"$                        uk.gov.hmrc.automatedexportsystemfrontend.controllers.$className$Controller.onPageLoad(mode: uk.gov.hmrc.automatedexportsystemfrontend.models.Mode = uk.gov.hmrc.automatedexportsystemfrontend.models.NormalMode)" >> ../conf/app.routes
echo "POST       /$className;format="decap"$                        uk.gov.hmrc.automatedexportsystemfrontend.controllers.$className$Controller.onSubmit(mode: uk.gov.hmrc.automatedexportsystemfrontend.models.Mode = uk.gov.hmrc.automatedexportsystemfrontend.models.NormalMode)" >> ../conf/app.routes

echo "GET        /change$className$                  uk.gov.hmrc.automatedexportsystemfrontend.controllers.$className$Controller.onPageLoad(mode: uk.gov.hmrc.automatedexportsystemfrontend.models.Mode = uk.gov.hmrc.automatedexportsystemfrontend.models.CheckMode)" >> ../conf/app.routes
echo "POST       /change$className$                  uk.gov.hmrc.automatedexportsystemfrontend.controllers.$className$Controller.onSubmit(mode: uk.gov.hmrc.automatedexportsystemfrontend.models.Mode = uk.gov.hmrc.automatedexportsystemfrontend.models.CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "$className;format="decap"$.title = $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.heading = $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.checkYourAnswersLabel = $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.error.required = Enter $className;format="decap"$" >> ../conf/messages.en
echo "$className;format="decap"$.error.length = $className$ must be $maxLength$ characters or less" >> ../conf/messages.en
echo "$className;format="decap"$.change.hidden = $className$" >> ../conf/messages.en

echo "Migration $className;format="snake"$ completed"
