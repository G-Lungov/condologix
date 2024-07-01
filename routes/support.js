var express = require('express');
var router = express.Router();

/* GET support page. */
router.get('/', function(req, res, next) {
  res.render('../views/support', { title: 'Support' });
});

module.exports = router;
