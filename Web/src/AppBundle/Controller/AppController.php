<?php

namespace AppBundle\Controller;

use Sensio\Bundle\FrameworkExtraBundle\Configuration\Route;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Method;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Template;

class AppController extends Controller
{
    /**
     * Home page
     *
     * @Route("/", name="homepage")
     * @Method("GET")
     * @Template()
     */
    public function indexAction()
    {
        if (false === $this->get('security.context')->isGranted('ROLE_USER')) {
            return $this->redirect($this->generateUrl('fos_user_security_login'));
        } else {
            return $this->redirect($this->generateUrl('post'));
        }

        return array();
    }

    /**
     * Help
     *
     * @Route("/help", name="help")
     * @Method("GET")
     * @Template()
     */
    public function helpAction()
    {
        return array();
    }
}
