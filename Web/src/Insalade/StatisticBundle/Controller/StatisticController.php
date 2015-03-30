<?php

namespace Insalade\StatisticBundle\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Route;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Template;

/**
 * Post controller.
 *
 * @Route("/statistic")
 */
class StatisticController extends Controller
{
    /**
     * @Route("/", name="statistic")
     * @Template()
     */
    public function indexAction()
    {
        $em = $this->getDoctrine()->getManager()->getConnection();

        //get user number
        $queryUserCount = $em->prepare("SELECT COUNT(*) FROM user");
        $queryUserCount->execute();
        $userCount = $queryUserCount->fetchAll();
        $nbUser = $userCount[0]['COUNT(*)'];

        //get android user number
        $queryAndroidUserCount = $em->prepare("SELECT COUNT(*) FROM user WHERE os=\"android\"");
        $queryAndroidUserCount->execute();
        $userCount = $queryAndroidUserCount->fetchAll();
        $nbAndroidUser = $userCount[0]['COUNT(*)'];

        //get ios user number
        $queryIOSUserCount = $em->prepare("SELECT COUNT(*) FROM user WHERE os=\"ios\"");
        $queryIOSUserCount->execute();
        $userCount = $queryIOSUserCount->fetchAll();
        $nbIOSUser = $userCount[0]['COUNT(*)'];

        //get ios user number
        $queryAssoCount = $em->prepare("SELECT COUNT(*) FROM asso");
        $queryAssoCount->execute();
        $userCount = $queryAssoCount->fetchAll();
        $nbAsso = $userCount[0]['COUNT(*)'];

        return array('data' => array('total' => $nbUser, 'android' => $nbAndroidUser, 'ios' => $nbIOSUser, 'asso' => $nbAsso));
    }
}
