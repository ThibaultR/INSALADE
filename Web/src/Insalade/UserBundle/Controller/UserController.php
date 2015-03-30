<?php

namespace Insalade\UserBundle\Controller;


use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Method;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Route;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Template;
use Insalade\UserBundle\Entity\User;

/**
 * User controller.
 *
 * @Route("/user")
 */
class UserController extends Controller
{

    /**
     * Lists all User entities.
     *
     * @Route("/", name="user")
     * @Method("GET")
     * @Template()
     */
    public function indexAction()
    {
        $em = $this->getDoctrine()->getManager();

        $entities = $em->getRepository('InsaladeUserBundle:User')->findBy(array(), array('id'=>'desc'));;

        $list = $this->get('av_list')->getList($entities, 'id', 'DESC', 'range', array(
            'max_per_page' => 10,
            'prev_message' => '<',
            'next_message' => '>',
            'sort' => 'id',
            'order' => 'DESC'
        ));

        return array(
            'list' => $list,
        );
    }

    /**
     * Update a User's enabled
     *
     * @Route("/{id}/updateenabled/{enabled}", name="user_update_enabled")
     * @Method("GET")
     */
    public function updateEnabledAction($id, $enabled)
    {
        $em = $this->getDoctrine()->getManager();
        $entity = $em->getRepository('InsaladeUserBundle:User')->find($id);
        $user = $this->container->get('security.context')->getToken()->getUser();
        if($this->get('security.context')->isGranted('ROLE_INSALADE')) {
            if (!$entity) {
                throw $this->createNotFoundException('Unable to find User entity.');
            }

            $entity->setEnabled($enabled);
            $em->flush();
        }

        return $this->redirect($this->generateUrl('user'));
    }
}
