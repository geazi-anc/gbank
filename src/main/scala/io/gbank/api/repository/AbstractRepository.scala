package io.gbank.api.repository

import reactivemongo.api.AsyncDriver
import reactivemongo.api.MongoConnectionOptions
/*      options = MongoConnectionOptions(
        nbChannelsPerNode = 150,
        //maxInFlightRequestsPerChannel = Some(1000)
 */
trait AbstractRepository:
  protected val driver     = AsyncDriver()
  protected val connection = driver.connect(List(sys.env.getOrElse("MONGO_URI", "localhost")))
