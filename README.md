# PassableShots

PK Camera App

Export your GPG key with

gpg2 --export --export-options export-clean,export-minimal --armor [your fingerprint] > passable.key

Then copy passable.key to the root of your SD card.

Each photo will be encrypted with the public key and written to the root of the SD card in the form
passable.[data+time in milliseconds]

## Roadmap

See Milestones under issues on github.

## Use Cases

You are traveling and do not want to be questioned about any photographs you have taken. By leaving
the private key at home, it is impossible for you (or anybody else) to decrypt the photographs
abroad.

## About the logo

The image from the logo is a photo of Hellfire Pass by Ian Armstrong (Flickr ianz) used under CC-BY-SA.

