var express = require('express');
var router = express.Router();

/* GET administrator page. */
router.get('/', function(req, res, next) {
  res.render('../views/administrator', { title: 'Administrator' });
});

module.exports = router;
