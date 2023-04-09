import { isEmpty, forEach, isNil } from 'lodash'

const FileMixin = {
    data: () => ({
      files: [],
    }),
    methods: {

      async filesUpload () {
        const resultFiles = await this.$store.dispatch('fileUploadRepeat', {
          files: this.files,
        })
        if (!isEmpty(resultFiles)) {
          resultFiles.forEach((info, index) => {
            let tmpKey = this
            const keys = info.target.split('.')

            keys.forEach((exKey, i) => {
              if (tmpKey[exKey]) {
                if (keys.length === (i + 1)) {
                  // downloadinfo 일 경우 key, url publicRead
                  if (exKey === 'downloadInfo') {
                    tmpKey[exKey] = {
                      key: info.key,
                      publicRead: info.publicRead,
                      url: info.url,
                    }
                  } else {
                    tmpKey[exKey] = info.url
                  }
                } else {
                  tmpKey = tmpKey[exKey]
                }
              }
            })
          })
        }
        console.log('filesUpload : ', resultFiles)
        // eslint-disable-next-line no-undef
        return resultFiles
      },
    },

}

/**
 * 파일 삭제 목록
 * @param {*} target 지워야 할 파일 push
 * @param {*} url 지워야 할 파일 url
 */
export const targetFiles = (target, url) => {
  if (isNil(url) || isEmpty(url)) return
  const keys = url.split('/')
  target.push({
    key: keys.slice(3).join('/'),
    url: url,
    publicRead: true,
  })
}

export default FileMixin
