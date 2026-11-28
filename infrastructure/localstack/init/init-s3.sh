#!/bin/bash

set -e

BUCKET_NAME="document-platform"

echo "Initializing S3 resources..."

if awslocal s3api head-bucket --bucket "$BUCKET_NAME" 2>/dev/null; then
    echo "Bucket $BUCKET_NAME already exists."
else
    awslocal s3 mb "s3://$BUCKET_NAME"
    echo "Bucket $BUCKET_NAME created successfully."
fi