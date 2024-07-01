var express = require('express');
var router = express.Router();

/* GET update-data page. */
router.get('/', function(req, res, next) {
  res.render('../views/update-data', { title: 'Update Data' });
});

module.exports = router;
