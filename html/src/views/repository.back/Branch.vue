
<script>
import tool from "@/common/js";
import http from "@/common/js/service";

export default {
  data: {},

  toCreateBranch(form) {
    let data = {};
    data.branch = "";
    data.frombranch = source.branch;

    this.$refs["branch-create"].showForm(data).then(data => {
      http.work("BRANCH_CREATE", data).then(result => {
        var value = result.value;
        if (result.errcode == 0) {
          app.toBranch(data.branch);
        }
      });
    });
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style  >
</style>
