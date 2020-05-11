<template>
  <div class="app-min-page" style="width: 1200px;">
    <div class="coos-row pd-20">
      <div class="coos-row pd-10 upgrade-body text-center">
        <template v-if="source.UPGRADE_STATUS == null || source.UPGRADE_STATUS=='NONE'">
          <div class="text-center font-lg pd-30 color-orange">系统更新检测中...</div>
        </template>
        <template v-else-if="source.UPGRADE_STATUS=='CHECKING'">
          <div class="text-center font-lg pd-30 color-orange">系统更新检测中...</div>
        </template>
        <template v-else-if="source.UPGRADE_STATUS=='CHECKED'">
          <template v-if="CHECK_RESULT.files.length > 0">
            <div class="text-center font-lg pd-30 color-red">检测到新版本！</div>
            <div class="coos-btn coos-btn-sm- bg-lime" @click="doDownload()">立即下载</div>
          </template>
          <template v-else>
            <div class="coos-text pd-30 color-grey">本地为最新版本，无需更新！</div>
          </template>
        </template>
        <template v-else-if="source.UPGRADE_STATUS=='DOWNLOADING'">
          <div class="text-center font-lg pd-30 color-orange">
            系统更新文件下载中...
            <!-- （
            <span>{{source.UPGRADE_DOWNLOAD.downloaded_count}}</span> /
            <span>{{source.UPGRADE_DOWNLOAD.count}}</span>）-->
          </div>
        </template>
        <template v-else-if="source.UPGRADE_STATUS=='DOWNLOADED'">
          <div class="text-center font-lg pd-30 color-green">系统更新文件下载完成</div>
          <div class="coos-btn coos-btn-sm- bg-lime" @click="doUpload()">立即更新</div>
        </template>
        <template v-else-if="source.UPGRADE_STATUS=='UPDATING'">
          <div class="text-center font-lg pd-30 color-orange">系统更新中...</div>
        </template>
        <template v-else-if="source.UPGRADE_STATUS=='UPDATED'">
          <div class="text-center font-lg pd-30 color-green">系统更新完成</div>
        </template>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "UpgradeIndex",
  data() {
    return {
      source: source,
      CHECK_RESULT: { files: [] }
    };
  },
  watch: {
    "source.UPGRADE_STATUS": function(UPGRADE_STATUS) {
      if (UPGRADE_STATUS == "DOWNLOADING") {
        window.setTimeout(() => {
          this.loadDownload();
        }, 300);
      }
    }
  },
  methods: {
    doDownload() {
      source.UPGRADE_STATUS = "DOWNLOADING";
      source.do("UPGRADE_DOWNLOAD").then(res => {});
    },
    doUpload() {
      source.UPGRADE_STATUS = "UPDATING";
      source.do("UPGRADE_UPDATE").then(res => {});
    },
    doCheck() {
      source.do("UPGRADE_CHECK", {}).then(res => {
        this.loadCheck();
      });
    },
    loadDownload() {
      if (this.loadDownloadIng) {
        return;
      }
      this.loadDownloadIng = true;

      if (source.UPGRADE_STATUS == "DOWNLOADING") {
        source.load("UPGRADE_DOWNLOAD", {}).then(res => {
          this.loadDownloadIng = false;
          window.setTimeout(() => {
            this.loadDownload();
          }, 500);
        });
      } else {
        source.load("UPGRADE_DOWNLOAD", {}).then(res => {
          this.loadDownloadIng = false;
        });
      }
    },
    loadCheck() {
      source.load("UPGRADE_CHECK", {}).then(res => {
        this.CHECK_RESULT.files.splice(0, this.CHECK_RESULT.files.length);
        res = res || {};
        res.files = res.files || [];
        res.files.forEach(f => {
          this.CHECK_RESULT.files.push(f);
        });
      });
    }
  },
  mounted() {
    this.doCheck();
    //this.check();
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
