import aws from '@/aws'
import { common } from '@/assets/enums'

const s3GetUrlMixin = {
  methods: {
    getSignedUrl (key, isWorkingAlbum) {
      if (key === null || key === common.EMPTY || key === undefined || key.startsWith(common.BLOB)) {
        return key
      }
      const s3 = isWorkingAlbum ? aws.s3 : aws.s3Client
      const params = {
        Bucket: s3.config.Bucket,
        Key: key,
      }
      return s3.getSignedUrl('getObject', params)
    },
  },
}

export default s3GetUrlMixin
