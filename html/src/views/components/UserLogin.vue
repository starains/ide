<template>
  <div class>
    <el-dialog
      :title="title"
      :visible.sync="show_choose"
      width="1000px"
      :close-on-click-modal="false"
    >
      <div class="coos-row">
        <div class="coos-row pd-10">
          <div class="float-right"></div>
        </div>
        <div class="coos-row pd-10">
          <el-table :data="list" style="width: 100%">
            <el-table-column prop="username" label="用户名" width="120"></el-table-column>
            <el-table-column prop="loginname" label="登录名" width="120"></el-table-column>
            <el-table-column prop="ip" label="IP" width="120"></el-table-column>
            <el-table-column prop="starttime" label="开始时间" width="180"></el-table-column>
            <el-table-column prop="endtime" label="结束时间" width="180"></el-table-column>
            <el-table-column label="地址">
              <template slot-scope="scope">
                <span>{{scope.row.province}}</span>
                <span>{{scope.row.city}}</span>
                <span>{{scope.row.district}}</span>
                <span>{{scope.row.street}}</span>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            layout="prev, pager, next"
            :total="totalcount"
            :page-size="pagesize"
            @current-change="load"
          ></el-pagination>
        </div>
        <div class="coos-row pd-10">
          <div class="float-right">
            <a class="coos-btn bg-grey" @click="chooseCancel()">取消</a>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  components: {},
  data() {
    return {
      title: "用户登录记录",
      list: [],
      source: source,
      show_choose: false,
      searchForm: { userid: "" },
      totalcount: 0,
      pagesize: 10,
      pageindex: 1
    };
  },
  methods: {
    show(data) {
      this.show_choose = true;
      data = data || {};
      this.searchForm.userid = data.userid;
      this.load(1);
      return new Promise((resolve, reject) => {
        this.resolve = resolve;
        this.reject = reject;
      });
    },
    chooseCancel() {
      this.show_choose = false;
    },
    load(pageindex) {
      coos.trimList(this.list);
      let data = this.searchForm;
      data.pageindex = pageindex || this.pageindex;
      data.pagesize = this.pagesize;
      source.load("USER_LOGINS", data).then(res => {
        this.pageindex = res.pageindex;
        this.totalcount = res.totalcount;
        res.value = res.value || [];
        res.value.forEach(one => {
          if (coos.isNotEmpty(one.starttime)) {
            one.starttime = source.formatByStr(
              one.starttime,
              "yyyy/MM/dd hh:mm"
            );
          }
          if (coos.isNotEmpty(one.endtime)) {
            one.endtime = source.formatByStr(one.endtime, "yyyy/MM/dd hh:mm");
          }
          this.list.push(one);
        });
      });
    }
  },
  mounted() {},
  beforeCreate() {}
};
</script>

<style  scoped="scoped">
</style>
