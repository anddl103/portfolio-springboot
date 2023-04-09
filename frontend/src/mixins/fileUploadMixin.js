import aws from '@/aws'
import { v4 as uuidv4 } from 'uuid'
import store from '@/store'

const fileUploadMixin = {
  data: () => ({
    imageAccept: 'image/jpeg, image/png, image/gif',
    videoAccept: 'video/mp4, video/mov, video/avi',
    initImgs: [],
  }),
  methods: {
    async imageUpload (path, filteredCards) {
      let res = true
      const totalUploadTarget = filteredCards.map(c => c.uploadedFiles).flatMap(t => t.filter(x => x.model !== null && x.model !== undefined))
      const totalUploadTargetCnt = totalUploadTarget.length
      let cnt = 0
      for await (const uf of totalUploadTarget) {
        const lIdx = uf.model.name.lastIndexOf('.')
        const idxFrom = lIdx - uf.model.name.length
        const ext = uf.model.name.slice(idxFrom)
        const name = uf.refname.concat('_', uuidv4()).concat('', ext)
        const key = 'v4/' + path + '/' + uf.cardId + '/' + name
        const acl = 'private'
        const param = {
          Bucket: aws.s3.config.Bucket,
          Key: key,
          ACL: acl,
          Body: uf.model,
          ContentType: uf.model.type,
        }
        await aws.s3.upload(param, async function (err) {
          if (err) res = false
        }).promise()
          .then(res => {
            uf.key = res.Key
            uf.modified = true
            cnt++
            store.commit('setLoadingValue', Math.round(cnt / totalUploadTargetCnt * 100))
          })
          .catch(err => {
            this.setError(err)
            store.dispatch('setLoadingFalse')
          })
      }
      return res
    },
    async uploadFiles (path, isAlbum) {
      let res = true
      const totalUploadTarget = this.uploadedFiles.filter(uf => uf.model !== null)
      const totalUploadTargetCnt = totalUploadTarget.length
      let cnt = 0
      for await (const uf of totalUploadTarget) {
        uf.reqUrl = uf.url
        const lIdx = uf.model.name.lastIndexOf('.')
        const idxFrom = lIdx - uf.model.name.length
        const ext = uf.model.name.slice(idxFrom)
        const name = uf.refname.concat('_', uuidv4()).concat('', ext)
        const key = 'v4/' + path + '/' + name
        const acl = 'private'
        const sdk = isAlbum ? aws.s3 : aws.s3Client
        const param = {
          Bucket: sdk.config.Bucket,
          Key: key,
          ACL: acl,
          Body: uf.model,
          ContentType: uf.model.type,
        }
        await sdk.upload(param, async function (err) {
          if (err) res = false
        }).promise()
          .then(res => {
            uf.reqUrl = uf.isKey ? res.Key : res.Location
            uf.key = res.Key
            cnt++
            store.commit('setLoadingValue', Math.round(cnt / totalUploadTargetCnt * 100))
          })
          .catch(err => {
            store.dispatch('setLoadingFalse')
            console.log(err)
        })
      }
      return res
    },
    setInitImgs () {
      const refs = []
      this.uploadedFiles.forEach(uf => {
        refs.push({
          refname: uf.refname,
          url: uf.url,
        })
      })
      this.initImgs = refs
    },
    async getInitImgs () {
      for await (const initImg of this.initImgs) {
        this.$refs[initImg.refname][0].fileInfo.url = initImg.url
      }
      for (const uf of this.uploadedFiles) {
        this.$set(uf, 'isValid', true)
      }
    },
    setIsValid (arr, refname, state) {
      const target = arr.find(uf => uf.refname === refname)
      if (target !== undefined) {
        target.isValid = state
      }
    },
    checkFilesValid (arr) {
      let res = true
      for (const f of arr) {
        if (f.rules.includes('required')) {
          const condition1 = f.model === null
          const condition2 = f.url === null || f.url === undefined || f.url === ''
          if (condition1 && condition2) {
            this.$set(f, 'isValid', false)
          }
        }
        if (f.passValidation) {
          this.$set(f, 'isValid', true)
        }
      }
      res = arr.filter(f => f.isValid === false).length <= 0
      return res
    },
  },
}

export default fileUploadMixin
