
# automated-export-system-frontend

This is the Automated Export System front end. 
It allows users in the Republic of Ireland and other countries in the EU to make IE507 submissions for goods passing through Northern Ireland.
It requires a CDS enrolment.

## Live examples
* /: the home page
* /mrn: the start page for adding a new submission
* /dashboard: view a list of all submissions made by the logged in user

### Run locally
To run locally, start the service `sbt run` which starts the system on port 5000, then run sm2 to start the dependencies.
The following sm2 command starts the service along with all its dependencies

`sm2 --start AUTOMATED_EXPORT_SERVICE_ALL`

Navigate to the [dashboard](http://localhost:5000/automated-export-system), which is the homepage.
The system will redirect to the stubs. See details on required enrolments [here](https://confluence.tools.tax.service.gov.uk/spaces/AES/pages/1344340004/Testing+using+the+auth+stubs).

### Test locally
`sbt test it/it`

### License

This code is open source software licensed under the [Apache 2.0 License](LICENSE).


