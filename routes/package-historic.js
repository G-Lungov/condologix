var express = require('express');
var router = express.Router();

/* GET package-historic page. */
router.get('/', function(req, res, next) {
  res.render('../views/package-historic', { title: 'Package Historic' });
});

module.exports = router;
