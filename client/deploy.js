import fs from "fs-extra";

const destinationFolder = "../lunchapp/src/main/resources/static";

fs.emptydirSync(destinationFolder);

fs.copy("dist", destinationFolder, function (err) {
  if (err) return console.error(err);
  console.log("success!");
});
