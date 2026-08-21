
# automated-export-system-frontend

This is the Automated Export System front end. It allows users in the Republic of Ireland and other countries to make IE507 submissions when passing through Northern Ireland.
It requires a CDS enrolment.


### Run locally
To run locally, start the service `sbt run` which starts the system on port 9000, then run sm2 to start the dependencies.
The following sm2 command starts the service along with all its dependencies

`sm2 --start AUTOMATED_EXPORT_SERVICE_ALL`

The system will redirect to the stubs. See details [here](https://confluence.tools.tax.service.gov.uk/spaces/AES/pages/1344340004/Testing+using+the+auth+stubs).


### Live examples

/automated-export-system/: This is the landing page


### Test locally
`sbt test it/it`




