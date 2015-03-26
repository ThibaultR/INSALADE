<?php

namespace Insalade\CommunicationBundle\Controller;

use Symfony\Component\HttpFoundation\Request;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Method;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Route;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\Template;
use Insalade\CommunicationBundle\Entity\Post;
use Insalade\CommunicationBundle\Form\PostType;

/**
 * Post controller.
 *
 * @Route("/post")
 */
class PostController extends Controller
{

    /**
     * Lists all Post entities.
     *
     * @Route("/", name="post")
     * @Method("GET")
     * @Template()
     */
    public function indexAction()
    {
        $em = $this->getDoctrine()->getManager();

        $entities = $em->getRepository('InsaladeCommunicationBundle:Post')->findBy(array(), array('id'=>'desc'));;

        $list = $this->get('av_list')->getList($entities, 'id', 'DESC', 'range', array(
            'max_per_page' => 10,
            'prev_message' => '<',
            'next_message' => '>',
            'sort' => 'id',
            'order' => 'DESC'
        ));

        $list->addColumn('id', null, 'Id', true, 'entity.id');
        $list->addColumn('association', null, 'Association', false, null);
        $list->addColumn('title', null, 'Title', false, null);
        $list->addColumn('dateStart', array(array("name" => "date", "params" => array('Y-m-d H:i:s'))), 'Start Date', false, null);
        $list->addColumn('dateEnd', array(array("name" => "date", "params" => array('Y-m-d H:i:s'))), 'End Date', false, null);
        $list->addColumn('dateCreate', array(array("name" => "date", "params" => array('Y-m-d H:i:s'))), 'Creation Date', false, null);
        $list->addColumn('state', null, 'State', true, 'entity.state');


        return array(
            'list' => $list,
        );
    }
    /**
     * Creates a new Post entity.
     *
     * @Route("/", name="post_create")
     * @Method("POST")
     * @Template("InsaladeCommunicationBundle:Post:new.html.twig")
     */
    public function createAction(Request $request)
    {
        $entity = new Post();
        $form = $this->createCreateForm($entity);
        $form->handleRequest($request);

        if ($form->isValid()) {
            $em = $this->getDoctrine()->getManager();

            $user = $this->container->get('security.context')->getToken()->getUser();
            $entity->setCreatorId($user->getId());

            $em->persist($entity);
            $em->flush();

            return $this->redirect($this->generateUrl('post_show', array('id' => $entity->getId())));
        }

        return array(
            'entity' => $entity,
            'form'   => $form->createView(),
        );
    }

    /**
     * Creates a form to create a Post entity.
     *
     * @param Post $entity The entity
     *
     * @return \Symfony\Component\Form\Form The form
     */
    private function createCreateForm(Post $entity)
    {
        $form = $this->createForm(new PostType(), $entity, array(
            'action' => $this->generateUrl('post_create'),
            'method' => 'POST',
            'attr' => array('id' => 'create-form')
        ));

        return $form;
    }

    /**
     * Displays a form to create a new Post entity.
     *
     * @Route("/new", name="post_new")
     * @Method("GET")
     * @Template()
     */
    public function newAction()
    {
        $entity = new Post();
        $form   = $this->createCreateForm($entity);

        return array(
            'entity' => $entity,
            'form'   => $form->createView(),
        );
    }

    /**
     * Finds and displays a Post entity.
     *
     * @Route("/{id}", name="post_show")
     * @Method("GET")
     * @Template()
     */
    public function showAction($id)
    {
        $em = $this->getDoctrine()->getManager();

        $entity = $em->getRepository('InsaladeCommunicationBundle:Post')->find($id);

        if (!$entity) {
            throw $this->createNotFoundException('Unable to find Post entity.');
        }

        return array(
            'entity'      => $entity
        );
    }

    /**
     * Displays a form to edit an existing Post entity.
     *
     * @Route("/{id}/edit", name="post_edit")
     * @Method("GET")
     * @Template()
     */
    public function editAction($id)
    {
        $em = $this->getDoctrine()->getManager();

        $entity = $em->getRepository('InsaladeCommunicationBundle:Post')->find($id);

        if (!$entity) {
            throw $this->createNotFoundException('Unable to find Post entity.');
        }

        $user = $this->container->get('security.context')->getToken()->getUser();

        if($entity->getState() == 'pushed' && !$this->get('security.context')->isGranted('ROLE_INSALADE')) {
            return $this->redirect($this->generateUrl('post_show', array('id' => $id)));
        }

        if(
            !$this->get('security.context')->isGranted('ROLE_INSALADE') &&
            !($this->get('security.context')->isGranted('ROLE_AMICALE') && $entity->getParentId() == $user->getId()) &&
            !($entity->getCreatorId() == $user->getId())) {

            return $this->redirect($this->generateUrl('post_show', array('id' => $id)));
        }

        $editForm = $this->createEditForm($entity);

        return array(
            'entity'      => $entity,
            'edit_form'   => $editForm->createView(),
        );
    }

    /**
    * Creates a form to edit a Post entity.
    *
    * @param Post $entity The entity
    *
    * @return \Symfony\Component\Form\Form The form
    */
    private function createEditForm(Post $entity)
    {
        $form = $this->createForm(new PostType(), $entity, array(
            'action' => $this->generateUrl('post_update', array('id' => $entity->getId())),
            'method' => 'PUT',
            'attr' => array('id' => 'edit-form')
        ));

        return $form;
    }
    /**
     * Edits an existing Post entity.
     *
     * @Route("/{id}", name="post_update")
     * @Method("PUT")
     * @Template("InsaladeCommunicationBundle:Post:edit.html.twig")
     */
    public function updateAction(Request $request, $id)
    {
        $em = $this->getDoctrine()->getManager();

        $entity = $em->getRepository('InsaladeCommunicationBundle:Post')->find($id);

        if (!$entity) {
            throw $this->createNotFoundException('Unable to find Post entity.');
        }

        $user = $this->container->get('security.context')->getToken()->getUser();

        if($entity->getCreatorId() != $user->getId() && !$this->get('security.context')->isGranted('ROLE_AMICALE')) {
            return $this->redirect($this->generateUrl('post_show', array('id' => $id)));
        }

        if($entity->getState() == 'pushed' && !$this->get('security.context')->isGranted('ROLE_INSALADE')) {
            return $this->redirect($this->generateUrl('post_show', array('id' => $id)));
        }

        $editForm = $this->createEditForm($entity);
        $editForm->handleRequest($request);

        if ($editForm->isValid()) {
            $entity->setState('waiting');
            $em->flush();

            return $this->redirect($this->generateUrl('post_show', array('id' => $id)));
        }

        return array(
            'entity'      => $entity,
            'edit_form'   => $editForm->createView(),
        );
    }
    /**
     * Deletes a Post entity.
     *
     * @Route("/{id}/delete", name="post_delete")
     * @Method("GET")
     */
    public function deleteAction($id)
    {
        $em = $this->getDoctrine()->getManager();
        $entity = $em->getRepository('InsaladeCommunicationBundle:Post')->find($id);

        if (!$entity) {
            throw $this->createNotFoundException('Unable to find Post entity.');
        }

        $user = $this->container->get('security.context')->getToken()->getUser();

        if(
            !$this->get('security.context')->isGranted('ROLE_INSALADE') &&
            !($this->get('security.context')->isGranted('ROLE_AMICALE') && $entity->getParentId() == $user->getId()) &&
            !($entity->getCreatorId() == $user->getId())) {

            return $this->redirect($this->generateUrl('post_show', array('id' => $id)));
        }

        $em->remove($entity);
        $em->flush();

        return $this->redirect($this->generateUrl('post'));
    }

    /**
     * Update a Post's state
     *
     * @Route("/{id}/updatestate/{state}", name="post_update_state")
     * @Method("GET")
     */
    public function updateStateAction($id, $state)
    {
        $em = $this->getDoctrine()->getManager();
        $entity = $em->getRepository('InsaladeCommunicationBundle:Post')->find($id);
        $user = $this->container->get('security.context')->getToken()->getUser();
        if(
            $this->get('security.context')->isGranted('ROLE_INSALADE') OR
            ($this->get('security.context')->isGranted('ROLE_AMICALE') && $entity->getParentId() == $user->getId())) {

            if (!$entity) {
                throw $this->createNotFoundException('Unable to find Post entity.');
            }

            $entity->setState($state);
            $em->flush();
        }

        return $this->redirect($this->generateUrl('post'));
    }

}
