import common from '@/common/js/common'


export default {
    data: {
        toText(data) {
            return common.http.post('api/data/toText', data);
        },
        toModel(data) {
            return common.http.post('api/data/toModel', data);
        },
        doTest(data) {
            return common.http.post('api/data/doTest', data);
        }
    }

}