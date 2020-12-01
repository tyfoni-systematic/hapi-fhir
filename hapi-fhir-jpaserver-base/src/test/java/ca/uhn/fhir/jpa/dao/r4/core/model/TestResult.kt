package ca.uhn.fhir.jpa.dao.r4.core.model

import com.google.gson.annotations.SerializedName
import java.util.*


data class TestResult(
   @SerializedName("errorCount") var errorCount: Int = 0,
   @SerializedName("warningCount") var warningCount: Int = NO_WARNING,
   @SerializedName("output") var output: List<String> = ArrayList()
) {
   companion object {
      const val NO_WARNING = Int.MIN_VALUE
   }
}
