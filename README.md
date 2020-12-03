## DirectFollowerMatrix
DirectFollowerMatrix - required data structure to filter and show direct follower matrix of activities.

### Usage:
```sh
$ sbt run
                            Initial diagnosis           Functional escalation       Incident closure            Incident classification     Investigation and diagnosis Incident logging            Resolution and recovery     
Initial diagnosis           0                           851                         268                         0                           0                           0                           1218                        
Functional escalation       0                           0                           0                           0                           851                         0                           0                           
Incident closure            0                           0                           0                           0                           0                           0                           0                           
Incident classification     2000                        0                           0                           0                           0                           0                           0                           
Investigation and diagnosis 337                         0                           97                          0                           0                           0                           417                         
Incident logging            0                           0                           0                           2000                        0                           0                           0                           
Resolution and recovery     0                           0                           1635                        0                           0                           0                           0                           
```
```sh
$ sbt 'run 2016-01-04T12:57:44+01:00[Europe/Paris] 2016-01-05T17:27:44+01:00[Europe/Paris]'
                            Initial diagnosis           Functional escalation       Incident closure            Incident classification     Investigation and diagnosis Incident logging            Resolution and recovery     
Initial diagnosis           0                           285                         163                         0                           0                           0                           584                         
Functional escalation       0                           0                           0                           0                           285                         0                           0                           
Incident closure            0                           0                           0                           0                           0                           0                           0                           
Incident classification     933                         0                           0                           0                           0                           0                           0                           
Investigation and diagnosis 99                          0                           45                          0                           0                           0                           141                         
Incident logging            0                           0                           0                           933                         0                           0                           0                           
Resolution and recovery     0                           0                           725                         0                           0                           0                           0                           
```
