import AWS, { Credentials } from 'aws-sdk'

const accessKeyId = process.env.VUE_APP_S3_ACC_KEY
const secretAccessKey = process.env.VUE_APP_S3_SEC_KEY
const credentials = new Credentials({
  accessKeyId, secretAccessKey,
})
const region = process.env.VUE_APP_AWS_REGION
const bucketClient = process.env.VUE_APP_S3_BUCKET_CLIENT
const bucket = process.env.VUE_APP_S3_BUCKET

AWS.config.update({ region: region, credentials: credentials })

export default {
  s3: new AWS.S3({ apiVersion: '2006-03-01', Bucket: bucket }),
  s3Client: new AWS.S3({ apiVersion: '2006-03-01', Bucket: bucketClient }),
}
