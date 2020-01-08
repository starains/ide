
<script>
import tool from "@/common/js";
import http from "@/common/js/service";

export default {
  mavenClean(data, callback) {
    http.work("MAVEN_CLEAN", data).then(result => {
      var value = result.value;
      callback && callback(result);
    });
  },
  mavenCompile(data, callback) {
    http.work("MAVEN_COMPILE", data).then(result => {
      var value = result.value;
      callback && callback(result);
    });
  },
  mavenPackage(data, callback) {
    http.work("MAVEN_PACKAGE", data).then(result => {
      var value = result.value;
      callback && callback(result);
    });
  },
  mavenInstall(data, callback) {
    http.work("MAVEN_INSTALL", data).then(result => {
      var value = result.value;
      callback && callback(result);
    });
  },
  mavenDeploy(data, callback) {
    http.work("MAVEN_DEPLOY", data).then(result => {
      var value = result.value;
      callback && callback(result);
    });
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style  >
</style>
